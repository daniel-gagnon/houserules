(ns houserules.routes
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary])
  (:require-macros [secretary.core :refer [defroute]]))

(def current-page (atom nil))

(defroute home-route "/" [] (reset! current-page :home))
(defroute admin-route "/admin" [] (reset! current-page :admin))
(defroute profile-route "/profile" [] (reset! current-page :profile))
(defroute register-route "/register" [] (reset! current-page :register))
(defroute register-details-route "/register/:token" [token] (reset! current-page :register-details))
(defroute sign-in-route "/sign/in" [] (reset! current-page :sign-in))
(defroute sign-out-route "/sign/out" [] (reset! current-page :sign-out))

(defn navigate-to [p]
  (.pushState js/history p "" p)
  (secretary/dispatch! p))

(aset js/window "onpopstate"
      (fn [event] (secretary/dispatch! (or (aget event "state") "/"))))

(let [href (.-href js/location)
      origin (.-origin js/location)]
  (secretary/dispatch! (.replace href origin "")))