(ns houserules.pages.register-details
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]
            [houserules.login :refer [full-name invalid-token?]]))

(defn password-strength [strength]
  [:div.ui.red.bottom.attached.progress
   [:div.bar {:height "0.25em"}]
   [:span "Password strength: " (["Terrible" "Weak" "So-so" "Good" "Strong"] strength)]] )

;<div class="ui bottom attached progress">
;<div class="bar"></div>
;</div>

(defn compute-strength [password] 0)

(defn register-details []
  [:div#register-details
   [:div.ui.attached.message
    [:div.header (if-not @invalid-token? "Registration" "Invalid token")]
    [:p (if-not @invalid-token? "Please complete your registration." "Your token is invalid or has expired.")]]
   [:div.ui.form.attached.fluid.segment
    (if-not @invalid-token?
      (let [password (atom "")
            strength (atom 0)]
        (add-watch password (gensym) (fn [_ _ _ pass] (reset! strength (compute-strength pass))))
        [:div.ui.form
         [:input {:placeholder "Name" :auto-focus true :on-change #(reset! full-name (let [n (-> % .-target .-value)] (when (not= n "") n)) )}]
         [:input {:placeholder "Password" :type :password}]
         [password-strength @strength]
         [:a {:href "http://xkcd.com/936/" :target "_blank"} "Advices for picking a strong and easy to remember password"]
         [:button.ui.green.button "Complete Registration"]])
      [:p "Please try registering again."])]])