(ns houserules.pages.sign-in
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [houserules.ajax :refer [POST]]
            [houserules.login :refer [register-user]]
            [houserules.routes :refer [navigate-to home-route]]))

(defn- send-login [email password in-flight email-error password-error]
  (reset! in-flight true)
  (POST "/auth/login"
    {:params {:email email
              :password password}
     :handler #(do (register-user %) (navigate-to (home-route)))
     :error-handler #(do (reset! in-flight false)
                         (reset! (case (:error (:response %))
                                  :user-doesn't-exist email-error
                                  :password-error password-error) true))}))

(defn sign-in []
  (let [email (atom "")
        password (atom "")
        in-flight (atom false)
        disable-button #(or @in-flight (some string/blank? [@email @password]))
        email-error (atom false)
        password-error (atom false)
        login #(do (send-login @email @password in-flight email-error password-error))
        clear-errors #(do (reset! email-error false) (reset! password-error false))]
    (fn []
      [:div#sign-in.ui.form
       [:div.ui.attached.message
        [:div.header "Sign in"]]
       [:div.ui.form.attached.fluid.segment
        [(if-not @email-error :div.field :div.field.error)
         [:input#email.ui {:value @email :placeholder "Email" :type :text :disabled @in-flight :auto-focus true :on-change #(do (clear-errors) (reset! email (string/trim (-> % .-target .-value))))}]]
        [(if-not @password-error :div.field :div.field.error)
         [:input#password.ui {:value @password :placeholder "Password" :type :password :disabled @in-flight :on-change #(do (clear-errors) (reset! password (-> % .-target .-value))) :on-key-down #(when (= 13 (.-keyCode %)) (login))}]]
        [(keyword (str "button#forgot-password.ui.button" (when (or (not (re-find #".+@.+\..+" @email)) @in-flight) ".disabled"))) {:disabled (or (not (re-find #".+@.+\..+" @email)) @in-flight)} "I forgot my password"]
        [(if-not (disable-button) :button.ui.primary.button :button.ui.primary.button.disabled) {:disabled (disable-button) :on-click login} "Login"]]])))