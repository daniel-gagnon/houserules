(ns houserules.widgets.popups
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.files :refer [hidden-file-selector file-drop-zone remove-files files]]
            [houserules.async :refer [darkroom?]]))

(def current-popup (atom nil))

(defn- clear-popup [] (reset! current-popup nil))

(defn crop-popup [title img]
  (let [first-file-is-image #(re-find #"image/.*" (or (:type (first @files)) ""))]
    [:div.window.ui.stacked.segment
     [:h2.ui.header title]
     [:div.content
      (when (darkroom?)
        [:div.placeholder (merge {:on-click #(.click (.querySelector js/document "div.window input[type=file]"))} file-drop-zone) "Click to upload a picture or drag and drop one here"])]
     [hidden-file-selector "images/*"]
     [:div.actions
      [(if (first-file-is-image) :button.ui.primary.button :button.ui.primary.button.disabled) {:disabled (not (first-file-is-image)) :on-click (comp #(reset! img (first @files)) remove-files clear-popup)} "Ok"]
      [:button.ui.button {:on-click (comp remove-files clear-popup)} "Cancel"]]]))