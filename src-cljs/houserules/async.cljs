(ns houserules.async
  (:require [reagent.core :refer [atom]]
            [houserules.ajax :refer [GET]]))

(def ^:private zxcvbn (atom nil))
(def ^:private recaptcha (atom nil))
(def ^:private darkroom (atom nil))

(defn wait-for [f atm]
  (let [interval-key (atom nil)]
    (reset! interval-key (.setInterval js/window #(when (aget js/window f) (.clearInterval js/window @interval-key) (reset! atm true)) 0))))

(defn async-load [urls atm f]
  (if-let [url (first urls)]
    (let [script-element (.createElement js/document "script")]
      (.appendChild (.-head js/document) script-element)
      (aset script-element "onload" #(async-load (rest urls) atm f))
      (aset script-element "src" url))
    (wait-for f atm)))

(defn zxcvbn? []
  (when (nil? @zxcvbn)
    (reset! zxcvbn false)
    (async-load ["/static/javascript/zxcvbn.js"] zxcvbn "zxcvbn")
    @zxcvbn))


(defn recaptcha? []
  (when (nil? @recaptcha)
    (reset! recaptcha false)
    (async-load ["https://www.google.com/recaptcha/api.js?render=explicit"] recaptcha "grecaptcha"))
  @recaptcha)

(defn darkroom? []
  (when (nil? @darkroom)
    (reset! darkroom false)
    (async-load ["/static/javascript/darkroom.min.js"] darkroom "Darkroom")
    @darkroom))