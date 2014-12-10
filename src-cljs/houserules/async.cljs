(ns houserules.async
  (:require [reagent.core :refer [atom]]
            [houserules.ajax :refer [GET]]))

(def ^:private zxcvbn (atom nil))
(def ^:private recaptcha (atom nil))
(def ^:private darkroom (atom nil))

(defn async-load [urls atm]
  (if-let [url (first urls)]
    (GET url {:handler
              #(do
                (js/eval %)
                (async-load (rest urls) atm))})
    (reset! atm true)))

(defn zxcvbn? []
  (when (nil? @zxcvbn)
    (reset! zxcvbn false)
    (async-load ["/static/javascript/zxcvbn.js"] zxcvbn)
    @zxcvbn))


(defn recaptcha? []
  (when (nil? @recaptcha)
    (reset! recaptcha false)
    (async-load ["https://www.google.com/recaptcha/api.js?render=explicit"] recaptcha))
  @recaptcha)

(defn darkroom? []
  (when (nil? @darkroom)
    (reset! darkroom false)
    (async-load ["/static/javascript/darkroom.min.js"] darkroom)
    @darkroom))