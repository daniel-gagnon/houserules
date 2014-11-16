(ns houserules.core
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.login :refer [email]]
            [houserules.routes :refer [current-page]]
            [houserules.topnav :refer [top-nav]]
            [houserules.pages.profile :refer [profile]]
            [houserules.pages.register :refer [register]]
            [houserules.pages.register-details :refer [register-details]]))

(defn welcome-message []
  (when (not (nil? @email))
    [:div.ui.message
     [:div.header "Welcome to Houserules"]
     [:p "Houserules is a tool to manage your LARPs and LARPing with the White Wolf™ franchises."]
     [:p "Please log in with a valid e-mail address."]]))

(defn site
  "The whole site"
  []
  [:div.container
   [top-nav]
   (if @email
     (case @current-page
       :home [:h1 "Home"]
       :profile [profile]
       :register-details [register-details]
       :admin [:h1 "Admin"]
       nil)
     (case @current-page
       :register [register]
       :register-details [register-details]
       [welcome-message]))])

(defn init! []
  (reagent/render-component [site] (.getElementById js/document "app")))