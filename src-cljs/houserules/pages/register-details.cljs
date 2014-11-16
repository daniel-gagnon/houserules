(ns houserules.pages.register-details
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]
            [houserules.login :refer [full-name invalid-token?]]))

(defn register-details []
  [:div#register-details
   [:div.ui.attached.message
    [:div.header (if-not @invalid-token? "Registration" "Invalid token")]
    [:p (if-not @invalid-token? "Please complete your registration." "Your token is invalid or has expired.")]]
   [:div.ui.form.attached.fluid.segment
    (if-not @invalid-token?
             [:input {:placeholder "Name" :on-change #(reset! full-name (let [n (-> % .-target .-value)] (when (not= n "") n)) )}]
             [:p "Please try registering again."])]])