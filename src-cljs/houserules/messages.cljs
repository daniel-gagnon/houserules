(ns houserules.messages
  (:require [reagent.core :as reagent :refer [atom]]))

(def msgs (atom []))

(defn messages []
  [:div (for [msg @msgs] msg)])

(defn remove-message [id]
  (swap! msgs (fn [coll] (filter #(not= (:id (meta %)) id) coll))))

(defn add-message [type header content]
  (let [id (gensym)]
    (swap! msgs conj ^{:id id}
                     [(keyword (str "div.ui.message." (name type)))
                      [:i.close.icon {:on-click #(remove-message id)}]
                      (when header
                        [:div.header header])
                      content])))