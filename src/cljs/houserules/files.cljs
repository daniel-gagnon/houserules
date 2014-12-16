(ns houserules.files
  (:require [reagent.core :as reagent :refer [atom]]))

(defrecord file [name size type data])
(defrecord file-progress [name size type loaded])

(def selected-files (atom nil))
(def files (atom nil))
(defn remove-files [] (reset! selected-files nil))

(defn read-file [f]
  (let [name (aget f "name")
        size (aget f "size")
        type (aget f "type")
        reader (js/FileReader.)
        atm (atom (->file-progress name size type 0))]
    (aset reader "onprogress" #(swap! atm assoc :loaded (aget % "loaded")))
    (aset reader "onload" #(reset! atm (->file name size type (aget reader "result"))))
    (.readAsDataURL reader f)
    atm))

(add-watch selected-files (gensym)
  (fn [_ _ _ new]
    (reset! files (mapv read-file new))))

(defn filelist->vector [filelist]
  (vec (for [idx (range (aget filelist "length"))] (.item filelist idx))))

(defn hidden-file-selector [accept]
  [:input {:type :file
           :accept accept
           :style {:display :none}
           :on-change #(reset! selected-files (-> % .-target .-files filelist->vector))}])

(defn prevent-default [e]
  (.stopPropagation e)
  (.preventDefault e))

(def file-drop-zone
  {:on-drop #(do (prevent-default %)
                 (reset! selected-files
                         (-> % .-nativeEvent
                             .-dataTransfer
                             .-files
                             filelist->vector))),
   :on-drag-enter prevent-default,
   :on-drag-over prevent-default})