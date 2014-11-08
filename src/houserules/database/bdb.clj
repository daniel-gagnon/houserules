(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry Transaction TransactionStats]
           [java.io File]))

(def ^:private shutting-down (atom false))
(.addShutdownHook (Runtime/getRuntime) (Thread. #(reset! shutting-down true)))

(def ^:private ^:dynamic *transaction* nil)

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

(defmacro with-transaction [& body]
  "Wraps in a transaction, adds a commit at the end if no commit is present"
  `(when (or *transaction* (not @shutting-down))
     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       ~@body
       ~@(when (not (some #{commit} (flatten body))) [commit]))))

(.addShutdownHook
  (Runtime/getRuntime)
  (Thread.
    #(when (realized? environment)
      (while (or (not @shutting-down) (pos? (.getNActive (.getTransactionStats @databases)))) (Thread/yield))
      (dorun
        (map (memfn close) (vals @databases))))))
