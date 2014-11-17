(ns houserules.ajax
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core]))

(def xsrf-token (atom nil))
(ajax.core/GET "/auth/xsrf" :handler #(reset! xsrf-token %))

(def HEAD  ajax.core/HEAD)
(def GET ajax.core/GET)

(defn- add-xsrf-token [{:keys [headers] :as options}]
  (assoc options :headers (merge headers {:X-CSRF-Token @xsrf-token})))

(defn POST [url options]
  (ajax.core/POST url (add-xsrf-token options)))

(defn PUT [url options]
  (ajax.core/PUT url (add-xsrf-token options)))

(defn DELETE [url options]
  (ajax.core/DELETE url (add-xsrf-token options)))

(defn OPTIONS [url options]
  (ajax.core/OPTIONS url (add-xsrf-token options)))

(defn TRACE [url options]
  (ajax.core/TRACE url (add-xsrf-token options)))

(defn PATCH [url options]
  (ajax.core/PATCH url (add-xsrf-token options)))