(ns houserules.pages.register
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.ajax :refer [POST]]))

(defn- fill-email [email-atom]
  (let [data (atom "")]
    [:div.ui.form.attached.fluid.segment
     [:input {:placeholder "e-mail" :auto-focus true :on-change #(reset! data (-> % .-target .-value)) :on-key-down #(when (= 13 (.-keyCode %)) (reset! email-atom @data))}]
     [:button.ui.green.button {:on-click #(reset! email-atom @data)} "Register"]]))

(defn- thank-you [email]
  [:div.ui.form.attached.fluid.segment
   [:p "Click the link that was sent at " email " to activate your account."]])

(defn register []
  (let [email (atom nil)]
    (add-watch email (gensym)
               (fn [_ _ _ email]
                 (POST "/auth/register"
                       {:params {:email email}})))
    (fn []
      [:div#register
       [:div.ui.attached.message
        [:div.header (if (not @email) "Register" "Thank you")]
        (when-not @email [:p "All you need is a valid e-mail address"])]
       (if (not @email) [fill-email email] [thank-you @email])])))