(ns houserules.login
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [houserules.routes :refer [navigate-to]]
            [reagent.cookies :as cookies]))

(def email (atom nil))
(def full-name (atom nil))
(def admin? (atom false))

(defn- register-user [user]
  "Register the user's email and name"
  (reset! email (user "email"))
  (reset! full-name (user "name"))
  (reset! admin? (user "admin")))

(defn logout []
  (reset! email false)
  (reset! full-name nil)
  (reset! admin? false)
  (cookies/remove! :houserules-session)
  (navigate-to "/"))

(GET "/auth/whoami"
     :handler #())

