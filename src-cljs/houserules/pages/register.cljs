(ns houserules.pages.register
  (:require [reagent.core :as reagent :refer [atom]]))

(defn register []
  [:div#register
   [:div.ui.attached.message
    [:div.header "Register"]
    [:p "All you need is a valid e-mail address"]]
   [:div.ui.form.attached.fluid.segment
    [:input {:placeholder "e-mail" :auto-focus true :on-key-down #(.log js/console (.-keyCode %))}]
    [:button.ui.green.button "Register"]]])