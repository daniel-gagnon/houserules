(ns houserules.async
  (:require [reagent.core :refer [atom]]))

(def zxcvbn (atom false))

(let [zxcvbn-id (atom nil)]
  (reset! zxcvbn-id (.setInterval js/window #(when (aget js/window "zxcvbn") (.clearInterval js/window @zxcvbn-id) (reset! zxcvbn true)) 100)))