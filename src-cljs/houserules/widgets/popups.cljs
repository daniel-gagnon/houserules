(ns houserules.widgets.popups
  (:require [reagent.core :as reagent :refer [atom]]))

(def current-popup (atom nil))

(defn crop-popup [title img]
  [:div.ui.active.modal
   [:i.close.icon]
   [:div.header title]
   [:div.content
    [:div.image "Image"]
    [:div.description "The description"]]
   [:div.actions
    [:button.ui.button "Cancel"]
    [:button.ui.button "Ok"]]])