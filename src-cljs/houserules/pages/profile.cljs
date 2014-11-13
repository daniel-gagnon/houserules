(ns houserules.pages.profile
  (:require [reagent.core :as reagent :refer [atom]]))

(defn row [label input]
  [:div.field
   [:label label]
   input])


(defn profile-form []
  [:div.ui.form
   (row "Name" [:input {:field :text, :id :name}])
   (row "Home Phone" [:input {:field :text, :id :home-phone}])
   (row "Mobile" [:input {:field :text, :id :mobile-phone}])
   (row "Address" [:textarea {:field :textarea, :id :address}])
   (row "Notes" [::textarea {:field :textarea, :id :notes}])

   ])

(defn profile []
  [:div
   [:h1.ui.header "Profile"]
   [:div.ui.grid
    [:div.four.wide.column
     [:div.ui.segment
      [profile-form]]]]])