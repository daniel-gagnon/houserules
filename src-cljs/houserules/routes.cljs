(ns houserules.routes
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary])
  (:import goog.History)
  (:require-macros [secretary.core :refer [defroute]]))

(def current-page (atom :home))

(defroute home-route "/" [] (reset! current-page :home))
(defroute admin-route "/admin" [] (reset! current-page :admin))
(defroute profile-route "/profile" [] (reset! current-page :profile))

(defn navigate-to [p]
  (.pushState js/history p "" p)
  (secretary/dispatch! p))

(aset js/window "onpopstate"
      (fn [event] (secretary/dispatch! (or (aget event "state") "/"))))