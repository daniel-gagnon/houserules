(ns houserules.routes
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [goog.history.EventType :as EventType])
  (:import goog.History)
  (:require-macros [secretary.core :refer [defroute]]))

(def page (atom :home))

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))

(defroute "/" []
          (.log js/console "There's nowhere like home")
          (reset! page :home))
