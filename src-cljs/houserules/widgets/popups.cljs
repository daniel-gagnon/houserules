(ns houserules.widgets.popups
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.files :refer [hidden-file-selector]]))

(def current-popup (atom nil))

(defn- clear-popup [] (reset! current-popup nil))

(defn crop-popup [title img]
  [:div.window.ui.stacked.segment
   [:h2.ui.header title]
   [:div.content
    [:div.placeholder {:on-click #(.click (.querySelector js/document "div.window input[type=file]"))} "Click to upload a picture or drag and drop one here"]]
   [hidden-file-selector "images/*"]
   [:div.actions
    [:button.ui.primary.button "Ok"]
    [:button.ui.button {:on-click clear-popup} "Cancel"]]])