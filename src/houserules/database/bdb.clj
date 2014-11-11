(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy]
            [slingshot.slingshot :refer [try+ throw+]])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry Transaction TransactionStats Database LockMode Cursor OperationStatus StatsConfig Transaction$State]
           [java.io File]
           [java.util.concurrent.locks Lock ReentrantReadWriteLock ReentrantReadWriteLock$WriteLock]
           [clojure.lang MapEntry]))

; The write lock is acquired and never released when the database is shutting down
; to prevent more transactions going in

(def ^:private ^ReentrantReadWriteLock rwlock (ReentrantReadWriteLock.))

(def ^:private ^:dynamic *transaction* nil)

(def ^:private ^:dynamic *database* nil)

(defmacro try-read-lock [& bodies]
  `(when (.tryLock (.readLock rwlock))
     (try+
       ~@bodies
       (finally (.unlock (.readLock rwlock))))))

(defn jam-lock []
  (.lock (.writeLock rwlock)))

(def ^:private environment
  (delay
    (let [db-folder (File. "database")]
      (.mkdir db-folder)
      (Environment.
        db-folder
        (doto (EnvironmentConfig.) (.setAllowCreate true) (.setTransactional true))))))

(def ^:private databases (atom {}))

(defn- open-database [name]
  (let [kw-name (keyword name)]
    (or
      (kw-name @databases)
      (let [db (.openDatabase ^Environment @environment *transaction* name (doto (DatabaseConfig.) (.setAllowCreate true) (.setTransactional true)))]
        (swap! databases assoc kw-name db)
        db))))

(defn clj->entry [data]
  (DatabaseEntry. (nippy/freeze data)))

(defn entry->clj [^DatabaseEntry entry]
  (nippy/thaw (.getData entry)))

(defn enum->keyword [^Enum enum]
  (keyword (.toLowerCase (.name enum))))

(defn commit []
  (.commit ^Transaction *transaction*))

(defn abort []
  (.abort ^Transaction *transaction*))


(defmacro with-transaction [& bodies]
  `(try-read-lock
     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       (try+
         ~@bodies
         (catch Object o# (do (when (= (.getState *transaction*) Transaction$State/OPEN) (abort)) (throw+ o#)))
         (finally (do (when (= (.getState *transaction*) Transaction$State/OPEN) (commit))))))))

(defmacro with-database [db & bodies]
  `(do
     (assert (~db @databases))
     (binding [*database* ~db]
       ~@bodies)))

(defn put
  ([k v] (put *database* k v))
  ([db k v]
   (assert (and (keyword? db)) (db @databases))
   (letfn [(inner-fn
             [db k v]
             (let [result (enum->keyword (.put (db @databases) *transaction* (clj->entry k) (clj->entry v)))]
               (if (= result :success)
                 :success
                 (throw+ {:error result
                          :database db
                          :key k
                          :value v}))))]
     (if *transaction*
       (inner-fn db k v)
       (with-transaction (inner-fn db k v))))))

(dorun (map
         (fn [[function method]]
           (eval
             `(defn ~function
                ([k# v#] (~function *database* k# v#))
                ([db# k# v#]
                 (assert (and (keyword? db#)) (db# @databases))
                 (letfn [(inner-fn#
                           [db# k# v#]
                           (let [result# (enum->keyword (~method (db# @databases) *transaction* (clj->entry k#) (clj->entry v#)))]
                             (if (= result# :success)
                               :success
                               (throw+ {:error result#
                                        :database db#
                                        :key k#
                                        :value v#}))))]
                   (if *transaction*
                     (inner-fn# db# k# v#)
                     (with-transaction (inner-fn# db# k# v#))))))))
         {'put '.put, 'put-no-overwrite '.putNoOverwrite, 'put-no-dup-data '.putNoDupData}))

(defn db-get
  ([key] (db-get *database* key))
  ([database key]
   (assert (and (keyword? database) (database @databases)))
   (letfn [(inner-fn
             [database key]
             (let [tmp-entry (DatabaseEntry.)
                   result (enum->keyword (.get (database @databases) *transaction* (clj->entry key) tmp-entry LockMode/DEFAULT))]
               (if (= result :success)
                 (entry->clj tmp-entry)
                 (throw+
                   {:error result
                    :database database
                    :key key}))))]
     (if *transaction*
       (inner-fn database key)
       (with-transaction (inner-fn database key))))))

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

(defn shutdown-database []
  (when (realized? environment)
    (jam-lock)
    (while (pos? (.getNActive (.getTransactionStats @environment StatsConfig/DEFAULT))) (Thread/yield))
    (dorun (map #(.close %) (vals @databases)))
    (.close @environment)))

(def ^:private migrations
  [#(open-database "users")])

(defn migrate []
  (with-transaction
    (open-database "migrations")))


