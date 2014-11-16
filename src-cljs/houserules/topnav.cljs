(ns houserules.topnav
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.routes :refer [current-page navigate-to home-route admin-route profile-route register-route sign-in-route sign-out-route]]
            [houserules.login :refer [email full-name admin?]]))

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
    [toolbar-button "Register" (not @email) "user" :register (register-route)]
    [toolbar-button "Sign in" (not @email) "sign.in" :sign-in (sign-in-route)]
    [toolbar-button (str "Sign out (" (or @full-name @email) ")") @email "sign.out" :sign-in (sign-out-route)]]])
