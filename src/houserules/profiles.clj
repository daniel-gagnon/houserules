(ns houserules.profiles
  (:require [houserules.database.bdb :refer [db-get put with-transaction with-database]]
            [noir.session :as session]))

(defn update-profile [options]
  (with-transaction
    (with-database
      :users
      (let [email (session/get :email)]
        (put email
             (merge (db-get email) options))))))