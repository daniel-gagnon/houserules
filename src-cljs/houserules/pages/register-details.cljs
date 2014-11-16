(ns houserules.pages.register-details
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]))

(defn register-details []
  [:div#register-details
   [:h1 "Register details"]])