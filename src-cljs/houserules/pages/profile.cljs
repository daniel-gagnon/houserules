(ns houserules.pages.profile
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.login :refer [full-name]]
            [reagent-forms.core :as forms]))

(defn row [label input]
  [:div.field
   [:label label]
   input])

(def profile-form
  [:div.ui.form
   [:div#picture-chooser
    [:div#picture-placeholder]
    [:button.ui.secondary.button "Upload a new avatar"]]
   [:div#contact
    (row "Name" [:input {:field :text, :id :name}])
    (row "Home Phone" [:input {:field :text, :id :home-phone}])
    (row "Mobile" [:input {:field :text, :id :mobile-phone}])
    (row "New Password" [:input {:field :password, :id :password}])]
   (row "Address" [:textarea {:field :textarea, :id :address}])
   (row "Notes" [::textarea {:field :textarea, :id :notes}])])

(defn profile []
  (let [doc (atom {:name @full-name})]
    [:div#profile-panel
     [:h1.ui.header "Profile"]
     [forms/bind-fields profile-form doc]]))