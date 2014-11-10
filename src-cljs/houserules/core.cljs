(ns houserules.core
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.routes :refer [current-page]]
            [houserules.login :refer [logged-in?]]
            [houserules.user :as user]))

(defn sign-out-text []
  [:span "Sign out (" (or @user/name @user/email) ")"])

(defn sign-in-out
  "Sign in or out section"
  []
  [:nav.right.menu
   (if @logged-in?
     [:a.item {:on-click #(.logout (.-id js/navigator))} [:i.sign.out.icon] [sign-out-text]]
     [:a.item {:on-click #(.request (.-id js/navigator))} [:i.sign.in.icon] "Sign in"])])

(defn top-nav
  "Navigation on top"
  []
  [:nav.ui.menu [:a.active.item [:i.home.icon] "Home"]  [sign-in-out]])

(defn site
  "The whole site"
  []
  [:div
   [top-nav]
   (case (current-page)
     :home [:h1 "Home"])])

(defn init! []
  (reagent/render-component [site] (.getElementById js/document "app")))