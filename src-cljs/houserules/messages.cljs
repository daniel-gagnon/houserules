(ns houserules.messages
  (:require [reagent.core :as reagent :refer [atom]]))

(def messages (atom []))

(defn messages-area []
  [:div (for [msg @messages] msg)])

(defn remove-message [id]
  (swap! messages (fn [coll] (filter #(not= (:id (meta %)) id) coll))))

(defn add-message [type header content]
  (let [id (gensym)]
    (swap! messages conj ^{:id id}
                     [(keyword (str "div.ui.message." (name type)))
                      [:i.close.icon {:on-click #(remove-message id)}]
                      (when header
                        [:div.header header])
                      content])))