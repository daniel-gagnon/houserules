(ns houserules.routes
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History)
  (:require-macros [secretary.core :refer [defroute]]))

(def ^:private page (atom :home))
(defn current-page [] @page)

(defroute "/" [] (reset! page :home))

(let [h (History.)]
  (events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (.setEnabled h true))

(let [pages {:home "/"}]
  (defn navigate-to [p]
    {:pre [(contains? (keys pages) p)]}
    (.pushState js/history nil "" (p pages))))
