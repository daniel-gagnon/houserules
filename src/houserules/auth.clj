(ns houserules.auth
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [noir.session :as session]
            [houserules.database.bdb :refer [db-get]]
            [houserules.settings :refer [owner secret-key]]
            [noir.util.crypt :refer [sha1-sign-hex]])
  (:import [org.joda.time DateTime]))

(defn logout [] (session/clear!))

(defn whoami [] (session/get :email))

(defn admin? []
  (= (session/get :email) @owner))

(defn verify-token [token]
  (session/clear!)
  (let [[email date hash] (.split token "~")
        valid-hash (sha1-sign-hex secret-key (str email date))]
    (when (and (= hash valid-hash) (re-seq #"^\d+$" date) (>= (Long/parseLong date) (.getMillis (DateTime.))))
      (session/put! :email email))))