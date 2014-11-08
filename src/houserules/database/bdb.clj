(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry Transaction TransactionStats Database LockMode]
           [java.io File]))

(def ^:private shutting-down (atom false))
(.addShutdownHook (Runtime/getRuntime) (Thread. #(reset! shutting-down true)))

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
         (map #(list (keyword %) (.openDatabase @environment nil % (.. (DatabaseConfig.) (setAllowCreate true) (setTransactional true)))))
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

(dorun (map
   (fn [[function method]]
     (eval
       `(defn ~function
          ([key# value#]
           (assert *database*)
           (~function key# value# *database*))
          ([key# value# database#]
           (assert *transaction*)
           (-> (~method database# *transaction* (clj->entry key) (clj->entry value#))
               .name
               .toLowerCase
               keyword)))))
     {'put '.put, 'put-no-overwrite '.putNoOverwrite, 'put-no-dup-data '.putNoDupData}))

(defn db-get
  ([key] (assert *database*) (get key *database*))
  ([key database] (assert *transaction*)
   (let [tmp-entry (DatabaseEntry.)]
     (.get database *transaction* (clj->entry key) tmp-entry LockMode/DEFAULT)
     (entry->clj tmp-entry))))

(defmacro with-transaction [& body]
  "Wraps in a transaction, adds a commit at the end if no commit is present"
  `(when (or *transaction* (not @shutting-down))
     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       ~@body
       ~@(when (not (some #{commit} (flatten body))) [commit]))))

(defmacro with-database [db & bodies]
  `(binding [*database* (db @databases)]
     ~@bodies))

(.addShutdownHook
  (Runtime/getRuntime)
  (Thread.
    #(when (realized? environment)
      (while (or (not @shutting-down) (pos? (.getNActive (.getTransactionStats @databases)))) (Thread/yield))
      (dorun
        (map (memfn close) (vals @databases))))))
