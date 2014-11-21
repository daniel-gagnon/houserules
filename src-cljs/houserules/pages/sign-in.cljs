(ns houserules.pages.sign-in
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]))

(defn- send-login [email password in-flight])

(defn sign-in []
  (let [email (atom "")
        password (atom "")
        in-flight (atom false)
        disable-button #(or @in-flight (some string/blank? [@email @password]))]
    (fn []
      [:div#sign-in
       [:div.ui.attached.message
        [:div.header "Sign in"]]
       [:div.ui.form.attached.fluid.segment
        [:input.ui.input {:placeholder "Email" :disabled @in-flight :auto-focus true :on-change #(reset! email (string/trim (-> % .-target .-value)))}]
        [:input.ui.input {:placeholder "Password" :type :password :disabled @in-flight :on-change #(reset! password (-> % .-target .-value))}]
        [(if-not (disable-button) :button.ui.green.button :button.ui.green.button.disabled) {:disabled (disable-button) :on-click #(do (send-login email password in-flight))} "Login"]]])))