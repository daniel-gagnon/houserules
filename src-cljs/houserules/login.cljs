(ns houserules.login
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(def logged-in? (atom false))

(defn- on-login [assertion]
  (POST "/auth/login"
        {:params {:assertion assertion}
         :handler #(reset! logged-in? true)
         :error-handler #(do (js/alert "Login eror") (.log js/console %) (.logout (.-id js/navigator)))}))

(defn- on-logout []
  (POST "/auth/logout"
        {:handler #(reset! logged-in? false)
         :error-handler #(do (js/alert "Logout eror") (.log js/console %))}))

(GET "/auth/whoami"
     :handler
     (fn [{:keys [logged email]}]
       (.watch (.-id js/navigator) (js-obj "loggedInUser" (if logged email nil), "onlogin" on-login, "onlogout" on-logout))))

