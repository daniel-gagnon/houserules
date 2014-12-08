(ns houserules.widgets.popups
  (:require [reagent.core :as reagent :refer [atom]]))

(def current-popup (atom nil))

(defn- clear-popup [] (reset! current-popup nil))

(defn crop-popup [title img]
  [:div.popup
   [:i.close.icon {:on-click clear-popup}]
   [:div.header title]
   [:div.content
    [:img {:src "http://placekitten.com/g/500/500"}]]
   [:div.actions
    [:button.ui.button {:on-click clear-popup} "Cancel"]
    [:button.ui.primary.button "Ok"]]])