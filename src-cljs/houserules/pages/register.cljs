(ns houserules.pages.register
  (:require [reagent.core :as reagent :refer [atom]]))

(defn fill-email [email-atom]
  [:div.ui.form.attached.fluid.segment
   [:input {:placeholder "e-mail" :auto-focus true :on-key-down #(.log js/console (.-keyCode %))}]
   [:button.ui.green.button "Register"]])

(defn thank-you [email]
  [:div.ui.form.attached.fluid.segment
   [:p "Click the link that was sent at " email " to activate your account."]])

(defn register []
  (let [email (atom nil)]
    (fn []
      [:div#register
       [:div.ui.attached.message
        [:div.header (if (not @email) "Register" "Thank you")]
        (when-not @email [:p "All you need is a valid e-mail address"])]
       (if (not @email) [fill-email email] [thank-you @email])])))