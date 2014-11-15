(ns houserules.topnav
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.routes :refer [current-page navigate-to home-route admin-route profile-route]]
            [houserules.login :refer [email full-name admin?]]))

(defn sign-out-text []
  [:span "Sign out (" (or @email @full-name) ")"])

(defn sign-in-out
  "Sign in or out section"
  []
  (if @email
    [:a.item [:i.sign.out.icon] [sign-out-text]]
    [:a.item [:i.sign.in.icon] "Sign in"])
  )

(defn toolbar-button [caption pred icon kw url]
  (when pred
    [(if (= @current-page kw) :a.active.item :a.item) {:on-click #(navigate-to url)} [(keyword (str "i." icon ".icon"))] caption]))

(defn top-nav
  "Navigation on top"
  []
  [:nav.ui.menu [toolbar-button "Home" @email "home" :home (home-route)]
   [:nav.right.menu
    [toolbar-button "Profile" @email "user" :profile (profile-route)]
    [toolbar-button "Admin" @admin? "settings" :admin (admin-route)]
    [sign-in-out]]])
