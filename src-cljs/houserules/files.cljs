(ns houserules.files
  (:require [reagent.core :refer [atom]]))

(def selected-files (atom nil))

(defn hidden-file-selector [accept]
  [:input {:type :file
           :accept accept
           :style {:display :none}
           :on-change #(reset! selected-files (-> % .-target .-files))}])