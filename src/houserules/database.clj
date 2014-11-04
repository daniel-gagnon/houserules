(ns houserules.database
  (:require [bitemyapp.revise.connection :refer [connect close]]
            [bitemyapp.revise.query :as r]
            [bitemyapp.revise.core :refer [run run-async]]))

(def ^:private conn (connect))

(defmacro with-db [& bodies]
  `(-> (r/db "houserules") ~@bodies (run conn)))

