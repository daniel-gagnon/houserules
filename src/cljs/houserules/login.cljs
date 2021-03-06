(ns houserules.login
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.ajax :refer [GET POST]]
            [houserules.routes :refer [navigate-to current-page]]
            [reagent.cookies :as cookies]
            [houserules.messages :refer [messages]]))

;; Email is nil (we don't know yet if the user is logged), false (the user is falsed) or a string with the email
(def email (atom nil))
(def full-name (atom nil))
(def admin? (atom false))
(def invalid-token? (atom nil))
(def recaptcha-sitekey (atom nil))

(defn- register-user [user]
  "Register the user's email and name"
  (reset! email (:email user))
  (reset! full-name (:name user))
  (reset! admin? (:admin? user)))


(defn sign-out []
  (POST "/auth/logout"
        {:handler (fn [_]
                    (reset! email false)
                    (reset! full-name nil)
                    (reset! admin? false)
                    (reset! messages [])
                    (navigate-to "/"))}))

(add-watch current-page (gensym) (fn [_ _ _ page] (when (= page :sign-out) (sign-out))))

(GET "/auth/whoami"
     :handler (fn [response]
                (reset! email (or (:email response) false))
                (reset! admin? (:admin? response))
                (reset! invalid-token? (:invalid-token? response))))

(GET "/auth/recaptcha/sitekey"
     :handler (fn [response]
                (reset! recaptcha-sitekey response)))
