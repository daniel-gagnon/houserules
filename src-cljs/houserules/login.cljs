(ns houserules.login
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(def email (atom nil))
(def full-name (atom nil))

(defn- register-user [user]
  (reset! email (user "email"))
  (reset! full-name (user "name")))

(defn- on-login [assertion]
  (POST "/auth/login"
        {:params {:assertion assertion}
         :handler register-user
         :error-handler #(do (js/alert "Login eror") (.log js/console %) (.logout (.-id js/navigator)))}))

(defn- on-logout []
  (POST "/auth/logout"
        {:handler #(do (reset! email nil) (reset! full-name nil))
         :error-handler #(do (js/alert "Logout eror") (.log js/console %))}))

(GET "/auth/whoami"
     :handler
     (fn [{:keys [logged email]}]
       (.watch (.-id js/navigator) (js-obj "loggedInUser" (if logged email nil), "onlogin" on-login, "onlogout" on-logout))))

