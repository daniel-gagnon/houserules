(ns houserules.auth
  (:refer-clojure :exclude [compare])
  (:require [noir.session :as session]
            [houserules.database.bdb :refer [db-get]]
            [houserules.settings :refer [owner secret-key]]
            [noir.util.crypt :refer [sha1-sign-hex compare]]
            [houserules.database.bdb :refer [db-get put]]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [houserules.settings :as settings])
  (:import [org.joda.time DateTime]))

(defn logout [] (session/clear!))

(defn whoami [] (session/get :email))

(defn admin? []
  (= (session/get :email) @owner))

(defn invalid-token? [] (session/get :token-invalid))

(defn get-user [email]
  (db-get email :database :users :default nil))

(defn verify-token [token action]
  (session/clear!)
  (let [[email date hash] (.split token "~")
        valid-hash (sha1-sign-hex secret-key (str email date (:password (get-user email))))]
    (let [user (get-user email)]
      (cond
        (and user (= action :register)) :already-registered
        (and
          (= hash valid-hash)
          (re-seq #"^\d+$" date)
          (>= (Long/parseLong date) (.getMillis (DateTime.))))
        (do
          (session/put! :email email)
          (when (= action :register)
            (put :users email {}))
          :token-valid)
        :else (do (session/put! :token-invalid true) :token-invalid)))))

(defn verify-password [email password]
  (when-let [db-password (:password (db-get email :database :users :default nil))]
    (compare password db-password)))

(defn valid-recaptcha? [response]
  (-> (str "https://www.google.com/recaptcha/api/siteverify?secret=" @settings/recaptcha-secret "&response=" response)
      (http/get {:as :text})
      deref
      :body
      json/read-str
      (get "success")))
