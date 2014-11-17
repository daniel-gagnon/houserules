(ns houserules.messages
  (:require [reagent.core :as reagent :refer [atom]]))

(def msgs (atom []))

(defn messages []
  [:div (when (pos? (count @msgs)) @msgs)])

(defn add-message [type header content]
  (let [id (gensym)]
    (swap! msgs conj ^{:id id}
                     [(keyword (str "div.ui.message." (name type)))
                      [:i.close.icon {:on-click (swap! msgs (fn [coll] (filter #(not= (:id (meta %)) id) coll)))}]
                      (when header
                        [:div.header header])
                      content])))