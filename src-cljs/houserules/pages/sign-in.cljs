(ns houserules.pages.sign-in
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [houserules.ajax :refer [POST]]
            [houserules.login :refer [register-user]]
            [houserules.routes :refer [navigate-to home-route]]))

(defn- send-login [email password in-flight]
  (reset! in-flight true)
  (POST "/auth/login"
    {:params {:email email
              :password password}
     :handler #(do (register-user %) (navigate-to (home-route)))
     :handle-error #(println %1 %2)}))

(defn sign-in []
  (let [email (atom "")
        password (atom "")
        in-flight (atom false)
        disable-button #(or @in-flight (some string/blank? [@email @password]))
        login #(do (send-login @email @password in-flight))]
    (fn []
      [:div#sign-in
       [:div.ui.attached.message
        [:div.header "Sign in"]]
       [:div.ui.form.attached.fluid.segment
        [:input.ui.input {:placeholder "Email" :disabled @in-flight :auto-focus true :on-change #(reset! email (string/trim (-> % .-target .-value))) :on-key-down #(when (= 13 (.-keyCode %)) (login))}]
        [:input.ui.input {:placeholder "Password" :type :password :disabled @in-flight :on-change #(reset! password (-> % .-target .-value)) :on-key-down #(when (= 13 (.-keyCode %)) (login))}]
        [(keyword (str "button#forgot-password.ui.secondary.button" (when-not (re-find #".+@.+\..+" @email) ".disabled"))) {:disabled (when-not (re-find #".+@.+\..+" @email) ".disabled")} "I forgot my password"]
        [(if-not (disable-button) :button.ui.primary.button :button.ui.primary.button.disabled) {:disabled (disable-button) :on-click login} "Login"]]])))

; (when (= 13 (.-keyCode %)))