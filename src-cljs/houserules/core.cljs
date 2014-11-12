(ns houserules.core
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.routes :refer [current-page]]
            [houserules.topnav :refer [top-nav]]))

(defn site
  "The whole site"
  []
  [:div
   [top-nav]
   (case @current-page
     :home [:h1 "Home"])])

(defn init! []
  (reagent/render-component [site] (.getElementById js/document "app")))