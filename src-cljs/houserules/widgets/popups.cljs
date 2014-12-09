(ns houserules.widgets.popups
  (:require [reagent.core :as reagent :refer [atom]]))

(def current-popup (atom nil))

(defn- clear-popup [] (reset! current-popup nil))

(defn crop-popup [title img]
  [:div.window.ui.stacked.segment
   [:h2.ui.header title]
   [:div.content
    [:div.placeholder "Click to upload a picture or drag and drop one here"]]
   [:div.actions
    [:button.ui.primary.button "Ok"]
    [:button.ui.button {:on-click clear-popup} "Cancel"]]])