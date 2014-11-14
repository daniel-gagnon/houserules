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

(defn- on-login [assertion]
  "Called by persona on a succesful login. Go fetch session credentials on the backend."
  (POST "/auth/login"
        {:params {:assertion assertion}
         :handler register-user
         :error-handler #(do (js/alert "Login eror") (.log js/console %) (.logout (.-id js/navigator)))}))

(defn- on-logout []
  "Called by persona on logout. Destroy session."
  (reset! email false)
  (reset! full-name nil)
  (reset! admin? false)
  (cookies/remove! :houserules-session)
  (navigate-to "/"))

(GET "/auth/whoami"
     :handler
     (fn [{:keys [logged email]}]
       (.watch (.-id js/navigator) (js-obj "loggedInUser" (if logged email nil), "onlogin" on-login, "onlogout" on-logout))))

