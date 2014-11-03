(ns houserules.core
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.routes :refer [page]]))

(defn site
  "The whole site"
  []
  (case @page
    :home [:h1 "Home"]))

(defn init! []
  (reagent/render-component [site] (.getElementById js/document "app")))