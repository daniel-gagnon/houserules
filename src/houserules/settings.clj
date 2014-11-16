(ns houserules.settings
  (:require [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [slingshot.slingshot :refer [try+ throw+]])
  (:import [java.io FileNotFoundException]))

(def owner (atom nil))
(def system (atom nil))

(def host (atom nil))
(def port (atom nil))
(def username (atom nil))
(def password (atom nil))

(def secret-key
  (String.
    (if (.exists (clojure.java.io/as-file "secret-key"))
      (byte-array (read-string (slurp "secret-key")))
      (let [secret-key (byte-array (repeatedly 16 #(rand-int 256)))]
        (spit "secret-key" (pr-str (vec secret-key)))
        secret-key))
    "UTF-8"))

(defn read-settings []
  (try+
    (let [data (yaml/parse-string (slurp "settings.yaml"))]
      (reset! owner (get-in data [:e-mail :owner]))
      (reset! system (get-in data [:e-mail :system]))
      (reset! host (get-in data [:smtp :host]))
      (reset! port (get-in data [:smtp :port]))
      (reset! username (get-in data [:smtp :username]))
      (reset! password (get-in data [:smtp :password]))
      true)
    (catch FileNotFoundException _ false)))


(defn write-default-settings []
  (spit "settings.yaml" (slurp  (io/resource "default-settings.yaml"))))