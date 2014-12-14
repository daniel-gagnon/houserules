(ns houserules.files
  (:require [reagent.core :as reagent :refer [atom]]))

(def selected-files (atom nil))
(def image (atom nil))
(defn remove-files [] (reset! selected-files nil))

(def revoke-object-url (-> js/window .-URL .-revokeObjectURL))
(def create-object-url (-> js/window .-URL .-createObjectURL))

(defn revoke-objects [objects]
  (doseq [o objects] (revoke-object-url o)))

(add-watch selected-files (gensym)
  (fn [_ _ old _]
    (revoke-objects old)))

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