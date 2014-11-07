(ns houserules.database.bdb
  (:require [taoensso.nippy :as nippy])
  (:import [com.sleepycat.je Environment EnvironmentConfig DatabaseConfig DatabaseEntry]
           [java.io File]))

(def ^:private shutting-down (ref false))
(def ^:private in-flight (ref 0))

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
