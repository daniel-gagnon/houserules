(ns houserules.core
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.login :refer [email]]
            [houserules.routes :refer [current-page]]
            [houserules.topnav :refer [top-nav]]
            [houserules.pages.profile :refer [profile]]))

(defn welcome-message []
  [:div.ui.message
   [:div.header "Welcome to Houserules"]
   [:p "Houserules is a tool to manage your LARPs and LARPing with the White Wolf™ franchises."]
   [:p "Please log in with a valid e-mail address."]])

(defn site
  "The whole site"
  []
  [:div.container
   [top-nav]
   (if @email
     (case @current-page
       :home [:h1 "Home"]
       :profile [profile]
       :admin [:h1 "Admin"]
       nil)
     [welcome-message])])

(defn init! []
  (reagent/render-component [site] (.getElementById js/document "app")))