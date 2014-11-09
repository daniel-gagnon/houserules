(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy]
            [slingshot.slingshot :refer [try+ throw+]])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry Transaction TransactionStats Database LockMode Cursor OperationStatus StatsConfig Transaction$State]
           [java.io File]
           [java.util.concurrent.locks Lock ReentrantReadWriteLock]
           [clojure.lang MapEntry]))

; The write lock is acquired and never released when the database is shutting down
; to prevent more transactions going in

(def ^:private rwlock (ReentrantReadWriteLock.))

(defmacro try-read-lock [& bodies]
  `(when (.tryLock (.readLock rwlock))
     (try+
       ~@bodies
       (finally (.unlock (.readLock rwlock))))))

(defn jam-lock []
  (.lock (.writeLock rwlock)))

(def ^:private ^:dynamic *transaction* nil)
(def ^:private ^:dynamic *database* nil)

(def ^:private environment
  (delay
    (let [db-folder (File. "database")]
      (.mkdir db-folder)
      (Environment.
        db-folder
        (.. (EnvironmentConfig.) (setAllowCreate true) (setTransactional true))))))

(def ^:private databases (atom {}))

(defn- open-database [name]
  (let [kw-name (keyword name)]
    (or
      (kw-name @databases)
      (let [db (.openDatabase @environment *transaction* name (.. (DatabaseConfig.) (setAllowCreate true) (setTransactional true)))]
        (swap! databases assoc kw-name db)
        db))))

(defn clj->entry [data]
  (DatabaseEntry. (nippy/freeze data)))

(defn entry->clj [entry]
  (nippy/thaw (.getData entry)))

(defn commit []
  (.commit *transaction*))

(defn abort []
  (.abort *transaction*))

(defn enum->keyword [enum]
  (keyword (.toLowerCase (.name enum))))

(dorun (map
   (fn [[function method]]
     (eval
       `(defn ~function
          ([key# value#]
           (~function key# value# *database*))
          ([key# value# database#]
           (assert (database# databases))
           (assert *transaction*)
           (let [result# (enum->keyword (~method (database# databases) *transaction* (clj->entry key) (clj->entry value#)))]
             (if (= result# :success)
               :success
               (throw+ {:error result#
                        :database database#
                        :key key#
                        :value value#})))))))
     {'put '.put, 'put-no-overwrite '.putNoOverwrite, 'put-no-dup-data '.putNoDupData}))

(defn db-get
  ([key] (db-get key *database*))
  ([key database]
   (assert (database databases))
   (assert *transaction*)
   (let [tmp-entry (DatabaseEntry.)
         result (enum->keyword (.get (database databases) *transaction* (clj->entry key) tmp-entry LockMode/DEFAULT))]
     (if (= result :success)
       (entry->clj tmp-entry)
       (throw+
         {:error result
          :database database
          :key key})))))

(defn db-seq
  ([] (db-seq *database*))
  ([database]
   (assert database)
   (let [cursor (.openCursor (database databases) *transaction* nil)]
     (take-while
       identity
       (repeatedly
         (fn []
           (let [k (DatabaseEntry.)
                 v (DatabaseEntry.)
                 result (.getNext cursor k v LockMode/DEFAULT)]
             (if (= result OperationStatus/SUCCESS)
               (MapEntry. (entry->clj k) (entry->clj v))
               (do (.close cursor) nil)))))))))

(defmacro with-transaction [& bodies]
  `(try-read-lock
     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       (try+
         ~@bodies
         (catch Object o# (do (when (= (.getState *transaction*) Transaction$State/OPEN) (abort)) (throw+ o#)))
         (finally (do (when (= (.getState *transaction*) Transaction$State/OPEN) (commit))))))))

(defmacro with-database [db & bodies]
  `(do
     (assert (db databases))
     (binding [*database* db]
       ~@bodies)))

(defn shutdown-database []
  (when (realized? environment)
    (jam-lock)
    (while (pos? (.getNActive (.getTransactionStats @environment StatsConfig/DEFAULT))) (Thread/yield))
    (dorun (map #(.close %) (vals @databases)))
    (.close @environment)))

(defn migrate []
  (with-transaction
    (open-database "migrations")))