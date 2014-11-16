(ns houserules.pages.register-details
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]
            [houserules.login :refer [full-name]]))

(defn register-details []
  [:div#register-details
   [:div.ui.attached.message
    [:div.header "Registration"]
    [:p "Please complete your registration."]]
   [:div.ui.form.attached.fluid.segment
    [:input {:placeholder "Name" :on-change #(reset! full-name (let [n (-> % .-target .-value)] (when (not= n "") n)) )}]]])