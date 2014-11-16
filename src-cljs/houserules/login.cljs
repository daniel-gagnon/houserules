(ns houserules.login
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [houserules.routes :refer [navigate-to current-page]]
            [reagent.cookies :as cookies]))

;; Email is nil (we don't know yet if the user is logged), false (the user is falsed) or a string with the email
(def email (atom nil))
(def full-name (atom nil))
(def admin? (atom false))

(defn- register-user [user]
  "Register the user's email and name"
  (reset! email (user "email"))
  (reset! full-name (user "name"))
  (reset! admin? (user "admin")))


(defn sign-out []
  (reset! email false)
  (reset! full-name nil)
  (reset! admin? false)
  (cookies/remove! :session)
  (navigate-to "/"))

(add-watch current-page (gensym) (fn [_ _ _ page] (when (= page :sign-out) (sign-out))))

(GET "/auth/whoami"
     :handler (fn [response]
                (reset! email (or (response "email") false))
                (reset! admin? (response "admin"))))

