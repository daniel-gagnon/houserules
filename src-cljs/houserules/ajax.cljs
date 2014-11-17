(ns houserules.ajax
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core]))

(def xsrf-token (atom nil))
(ajax.core/GET "/auth/xsrf" :handler #(reset! xsrf-token %))

(def HEAD  ajax.core/HEAD)
(def GET ajax.core/GET)

(defn POST [url {:keys [headers] :as options}]
  (ajax.core/POST url (assoc options :headers (merge headers {:X-CSRF-Token @xsrf-token}))))