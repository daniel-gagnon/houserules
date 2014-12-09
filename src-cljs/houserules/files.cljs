(ns houserules.files
  (:require [reagent.core :refer [atom]]))

(defn hidden-file-selector [accept]
  [:input {:type :file, :accept accept :style {:display :none}}])