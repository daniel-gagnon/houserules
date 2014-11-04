(ns houserules.database
  (:require [bitemyapp.revise.connection :refer [connect close]]
            [bitemyapp.revise.query :as r]
            [bitemyapp.revise.core :refer [run run-async]]))


(def conn
  (delay
    (let [c (connect)]
      (.addShutdownHook
        (Runtime/getRuntime)
        (Thread. #(close c)))
      c)))

(defmacro with-db [& bodies]
  `(-> (r/db "houserules") ~@bodies (run @conn)))

