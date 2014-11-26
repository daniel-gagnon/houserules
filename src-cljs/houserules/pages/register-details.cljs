(ns houserules.pages.register-details
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.ajax :refer [PUT]]
            [houserules.login :refer [full-name invalid-token? email]]
            [houserules.messages :refer [add-notification]]
            [houserules.routes :refer [navigate-to home-route]]))

(defn password-strength [strength]
  [:div.ui.bottom.attached.progress
   [:div.bar {:style {:background-color (["white" "red" "orange" "yellow" "green"] strength) :width (str (* strength 25) "%")}}]
   [:span "Password strength: " (["Terrible" "Weak" "So-so" "Good" "Strong"] strength)]] )

(defn compute-strength [password]
  (let [zxcvbn-score (js/zxcvbn password (js/Array @full-name @email))]
    (.-score zxcvbn-score)))

(defn register [password in-flight]
  (reset! in-flight true)
  (PUT "/profiles/update"
       {:params {:name @full-name :password @password}
        :handler #(do
                   (reset! in-flight false)
                   (add-notification :success "Registration complete" [:p "Welcome to Houserules!"])
                   (navigate-to (home-route)))}))

(defn register-details []
  (let [password (atom "")
        strength (atom 0)
        in-flight (atom false)]
    (add-watch password (gensym) (fn [_ _ _ pass]  (reset! strength (compute-strength pass))))
    (fn []
      [:div#register-details
       [:div.ui.attached.message
        [:div.header (if-not @invalid-token? "Registration" "Invalid token")]
        [:p (if-not @invalid-token? "Please complete your registration." "Your token is invalid or has expired.")]]
       [:div.ui.form.attached.fluid.segment
        (if-not @invalid-token?
          [:div.ui.form
           [:input.ui.input {:type :text :placeholder "Name" :disabled @in-flight :auto-focus true :on-change #(reset! full-name (let [n (-> % .-target .-value)] (when (not= n "") n)) )}]
           [:input.ui.input {:placeholder "Password" :disabled @in-flight :type :password :on-change #(reset! password (-> % .-target .-value))}]
           [password-strength @strength]
           [:a {:href "http://xkcd.com/936/" :target "_blank"} "Advices for picking a strong and easy to remember password"]
           [(keyword (str "button.ui.primary.button"(when (< @strength 2) ".disabled")))
            {:disabled (< @strength 2) :on-click #(register password in-flight)}
            "Complete Registration"]]
          [:p "Please try registering again."])]])))