(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy]
            [slingshot.slingshot :refer [throw+]])
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
  "Wraps in a transaction, adds a commit at the end if no commit is present"
  `(when (or *transaction* (not @shutting-down))
     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       ~@body
       ~@(when (not (some #{commit} (flatten body))) [commit]))))

(defmacro with-database [db & bodies]
  `(do
     (assert (db databases))
     (binding [*database* db]
       ~@bodies)))

(.addShutdownHook
  (Runtime/getRuntime)
  (Thread.
    #(when (realized? environment)
      (while (or (not @shutting-down) (pos? (.getNActive (.getTransactionStats @databases)))) (Thread/yield))
      (dorun
        (map (memfn close) (vals @databases))))))
