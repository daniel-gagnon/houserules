(ns houserules.pages.profile
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.login :refer [full-name]]
            [reagent-forms.core :as forms]
            [houserules.widgets.popups :refer [current-popup crop-popup]]))

(defn row [label input]
  [:div.field
   [:label label]
   input])

(def profile-form
  [:div.ui.form
   [:div#picture-chooser
    [:img {:src "http://placekitten.com/g/250/250"}]
    [:button.ui.secondary.button {:on-click #(reset! current-popup [crop-popup "Upload a new avatar" nil])} "Upload a new avatar"]]
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