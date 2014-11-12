(ns houserules.topnav
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.routes :refer [current-page]]
            [houserules.login :refer [email full-name admin?]]))

(defn sign-out-text []
  [:span "Sign out (" (or @email @full-name) ")"])

(defn sign-in-out
  "Sign in or out section"
  []
  (if @email
    [:a.item {:on-click #(.logout (.-id js/navigator))} [:i.sign.out.icon] [sign-out-text]]
    [:a.item {:on-click #(.request (.-id js/navigator))} [:i.sign.in.icon] "Sign in"])
  )

(defn admin []
  (when @admin?
    [:a.item [:i.settings.icon] "Admin"]))

(defn profile []
  (when @email
    [:a.item [:i.user.icon] "Profile"]))

(defn top-nav
  "Navigation on top"
  []
  [:nav.ui.menu [:a.active.item [:i.home.icon] "Home"]
   [:nav.right.menu
    [profile]
    [admin]
    [sign-in-out]]])
