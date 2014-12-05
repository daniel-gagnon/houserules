(ns houserules.async
  (:require [reagent.core :refer [atom]]))

(def zxcvbn (atom false))
(def recaptcha (atom true))

(defn wait-for [f atm]
  (let [interval-key (atom nil)]
    (reset! interval-key (.setInterval js/window #(when (aget js/window f) (.clearInterval js/window @interval-key) (reset! atm true)) 100))))

(wait-for "zxcvbn" zxcvbn)
;(wait-for "grecaptcha" recaptcha)