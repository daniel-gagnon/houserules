(ns houserules.files
  (:require [reagent.core :as reagent :refer [atom]]))

(def selected-files (atom nil))
(defn remove-files [] (reset! selected-files nil))

(defn hidden-file-selector [accept]
  [:input {:type :file
           :accept accept
           :style {:display :none}
           :on-change #(reset! selected-files (-> % .-target .-files))}])

(defn prevent-default [e]
  (.stopPropagation e)
  (.preventDefault e))

(def file-drop-zone
  {:on-drop #(do (prevent-default %) (reset! selected-files (-> % .-nativeEvent .-dataTransfer .-files ))), :on-drag-enter prevent-default, :on-drag-over prevent-default})