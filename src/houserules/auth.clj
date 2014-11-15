(ns houserules.auth
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [noir.session :as session]
            [houserules.database.bdb :refer [db-get]]))

(defn logout [] (session/clear!))

(defn whoami [] (session/get :email))

(defn admin? []
  (= (session/get :email) (db-get :owner :database :settings :default nil)))