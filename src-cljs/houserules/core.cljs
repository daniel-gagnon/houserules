(ns houserules.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defn init! []
  (reagent/render-component [:h1 "Hello World!"] (.getElementById js/document "app")))