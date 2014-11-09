(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy]
            [slingshot.slingshot :refer [try+ throw+]])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry Transaction TransactionStats Database LockMode]
           [java.io File]
           [java.util.concurrent.locks Lock ReentrantReadWriteLock]))

; The write lock is acquired and never released when the database is shutting down
; to prevent more transactions going in
(let [rwlock (ReentrantReadWriteLock.)]
  (defmacro try-read-lock [& bodies]
    (when (.tryLock (.readLock rwlock))
      (try+
        ~@bodies
        (finally (.unlock (.readLock rwlock))))))
  (defn jam-lock []
    (.lock (.writeLock rwlock))))

(def ^:private ^:dynamic *transaction* nil)
(def ^:private ^:dynamic *database* nil)

(def ^:private environment
  (delay
    (let [db-folder (File. "database")]
      (.mkdir db-folder)
      (Environment.
        db-folder
        (.. (EnvironmentConfig.) (setAllowCreate true) (setTransactional true))))))

(def ^:private databases
  (delay
    (->> (.getDatabaseNames @environment)
         (map #(list (keyword %) (.openDatabase @environment nil % (.. (DatabaseConfig.) (setAllowCreate false) (setTransactional true)))))
         flatten
         (apply hash-map))))

(defn clj->entry [data]
  (DatabaseEntry. (nippy/freeze data)))

(defn entry->clj [entry]
  (nippy/thaw (.getData entry)))

(defn commit [trx]
  (.commit trx))

(defn abort [trx]
  (.abort trx))

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

(defmacro with-transaction [& body]
  `(try-read-lock
     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       ~@body
       ~@(when (not (some #{commit} (flatten body))) [commit]))))

(defmacro with-database [db & bodies]
  `(do
     (assert (db databases))
     (binding [*database* db]
       ~@bodies)))

(defn shutdown-database []
  (when (realized? environment)
    (jam-lock)
    (while (pos? (.getNActive (.getTransactionStats @environment))) (Thread/yield))
    (dorun (map #(.close %) (vals @databases)))))
