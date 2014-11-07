(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry Transaction]
           [java.io File]))

(def ^:private shutting-down (ref false))
(def ^:private in-flight (ref 0))

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
  (.commit trx)
  (dosync (commute in-flight dec)))

(defn abort [trx]
  (.abort trx)
  (dosync (commute in-flight dec)))

(defmacro with-transaction [& body]
  "Wraps in a transaction, adds a commit at the end if no commit is present"
  `(when (dosync
           (ensure shutting-down)
           ; Child transaction can be acquired even when the app is shutting down but not parent transactions
           (when (or *transaction* (not @shutting-down))
             (commute in-flight inc)))

     (binding [*transaction* (.beginTransaction @environment *transaction* nil)]
       ~@body
       ~@(when (not (some #{commit} (flatten body))) [commit]))))

(.addShutdownHook (Runtime/getRuntime) (Thread. #(dosync (ref-set shutting-down true))))
(.addShutdownHook
  (Runtime/getRuntime)
  (Thread.
    #(do
      (while (or (not @shutting-down) (pos? @in-flight)) (Thread/yield))
      (when (realized? environment)
        (dorun
          (map (memfn close) (vals @databases)))
        (.close @environment)))))
