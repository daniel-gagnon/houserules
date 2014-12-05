(ns houserules.pages.register
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.ajax :refer [POST]]
            [clojure.string :as string]
            [houserules.async :as async]))

(defn send-registration [data email in-flight already-registered?]
  (reset! in-flight true)
  (POST "/auth/register"
        {:params {:email data}
         :handler (fn [response]
                    (reset! in-flight false)
                    (reset! already-registered? (not response))
                    (when-not @already-registered? (reset! email data)))}))

(def recaptcha
  (with-meta (fn [] [:div#recaptcha]) {:component-did-mount #(.render js/grecaptcha "recaptcha" (js-obj "theme" "light", "sitekey" "6Ld91f4SAAAAAJ7JT7xX2KuTiUs2gQvamJ-K8bDt"))} ))

(defn- fill-email [_ _ _]
  (let [data (atom "")]
    (fn [email in-flight already-registered?]
      [:div.ui.form.attached.fluid.segment
       [:input {:type :text :placeholder "e-mail" :auto-focus true :disabled @in-flight :on-change #(reset! data (-> % .-target .-value)) :on-key-down #(when (= 13 (.-keyCode %)) (reset! email @data))}]
       (when @async/recaptcha [recaptcha])
       [(if-not (or @in-flight (not (re-find #".+@.+\..+" @data))) :button.ui.primary.button :button.ui.primary.button.disabled) {:disabled (or @in-flight (= (string/trim @data) "")) :on-click #(do (send-registration (string/trim @data) email in-flight already-registered?))} "Register"]])))

(defn- thank-you [email]
  [:div.ui.form.attached.fluid.segment
   [:p "Click the link that was sent at " email " to activate your account."]])

(defn- already-registered []
  [:div.ui.form.attached.fluid.segment
   [:p "You can ask for a password reset e-mail on the \"Sign In\" page."]])

(defn register []
  (let [email (atom nil)
        in-flight (atom false)
        already-registered? (atom false)]
    (fn []
      [:div#register
       [:div.ui.attached.message
        [:div.header (cond @already-registered? "Already registered" (not @email) "Register" :else "Thank you")]
        (if @already-registered? [:p "Did you forget your password?"] (when-not @email [:p "All you need is a valid e-mail address"]))]
       (cond @already-registered? [already-registered] (not @email) [fill-email email in-flight already-registered?] :else [thank-you @email])])))