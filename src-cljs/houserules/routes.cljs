(ns houserules.routes
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History)
  (:require-macros [secretary.core :refer [defroute]]))

(def current-page (atom :home))

(defroute home-route "/" [] (reset! current-page :home))
(defroute admin-route "/admin" [] (reset! current-page :admin))
(defroute profile-route "/profile" [] (reset! current-page :profile))

(let [h (History.)]
  (events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (.setEnabled h true))

(defn navigate-to [p]
  (.pushState js/history nil "" p))