(ns houserules.routes.database.migrations
  (:require [houserules.database :refer [with-db conn]]
            [bitemyapp.revise.query :as r]
            [bitemyapp.revise.core :refer [run run-async]]))


(defn- create-db []
  (let [result (-> (r/db-create "houserules") (run @conn))]
    (assert (or (= (first (:response result)) {:created 1}) (.contains (apply str (:response result)) "already exists")))))

(defn create-migration-table []
  (let [result (with-db (r/table-create-db "migrations" :primary-key :email))]
    (assert (or (= (first (:response result)) {:created 1}) (.contains (apply str (:response result)) "already exists")))))

(defn- init []
  (create-db)
  (create-migration-table))

(defn migrate []
  (init))