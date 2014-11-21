(ns houserules.auth
  (:refer-clojure :exclude [compare])
  (:require [noir.session :as session]
            [houserules.database.bdb :refer [db-get]]
            [houserules.settings :refer [owner secret-key]]
            [noir.util.crypt :refer [sha1-sign-hex compare]]
            [houserules.database.bdb :refer [db-get put]])
  (:import [org.joda.time DateTime]))

(defn logout [] (session/clear!))

(defn whoami [] (session/get :email))

(defn admin? []
  (= (session/get :email) @owner))

(defn invalid-token? [] (session/get :token-invalid))

(defn get-user [email]
  (db-get email :database :users :default nil))

(defn verify-token [token]
  (session/clear!)
  (let [[email date hash] (.split token "~")
        valid-hash (sha1-sign-hex secret-key (str email date))]
    (let [user (get-user email)]
      (cond
        user :already-registered

        (and
          (= hash valid-hash)
          (re-seq #"^\d+$" date)
          (>= (Long/parseLong date) (.getMillis (DateTime.))))
        (do
          (put :users email {})
          (session/put! :email email)
          :token-valid)

        :else (do (session/put! :token-invalid true) :token-invalid)))))

(defn verify-password [email password]
  (when-let [user (db-get email :database :users :default nil)]
    (compare password (:password user))))