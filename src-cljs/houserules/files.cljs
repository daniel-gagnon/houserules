(ns houserules.files
  (:require [reagent.core :as reagent :refer [atom]]))

(defrecord file [name size type length data])

(def selected-files (atom nil))
(def files (atom nil))
(defn remove-files [] (reset! selected-files nil))

(defn revoke-objects-urls [objects]
  (doseq [o objects] ((aget js/window "URL" "revokeObjectURL") o)))

(defn create-objects-urls [objects]
  (->> objects
       (map (juxt
              #(aget % "name")
              #(aget % "size")
              #(aget % "type")
              #(aget % "length")
              (aget js/window "URL" "createObjectURL")))
       (map #(apply ->file %))))

(add-watch selected-files (gensym)
  (fn [_ _ old new]
    (revoke-objects-urls old)
    (reset! files (create-objects-urls new))))

(defn hidden-file-selector [accept]
  [:input {:type :file
           :accept accept
           :style {:display :none}
           :on-change #(reset! selected-files (-> % .-target .-files))}])

(defn prevent-default [e]
  (.stopPropagation e)
  (.preventDefault e))

(defn filelist->vector [filelist]
  (for [idx (range (aget filelist "length"))] ((aget filelist "item") idx)))

(def file-drop-zone
  {:on-drop #(do (prevent-default %)
                 (reset! selected-files
                         (-> % .-nativeEvent
                             .-dataTransfer
                             .-files
                             filelist->vector))),
   :on-drag-enter prevent-default,
   :on-drag-over prevent-default})