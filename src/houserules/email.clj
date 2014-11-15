(ns houserules.email
  (:require [clojure.java.io :as io]))

(def settings (atom nil))

(defn read-settings [] false)

(defn write-default-settings []
  (spit "settings.yaml" (slurp  (io/resource "default-settings.yaml"))))