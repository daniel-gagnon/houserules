(ns houserules.messages
  (:require [reagent.core :as reagent :refer [atom]]))

(def messages (atom []))
(def notifications (atom []))

(defn messages-area []
  [:div#messages (for [msg @messages] msg)])

(defn notifications-area []
  [:div#notifications (for [notif (reverse @notifications)] notif)])

(defn remove-message [coll id]
  (swap! coll (fn [coll] (filter #(not= (:id (meta %)) id) coll))))

(defn add-message [type header content]
  (let [id (gensym)]
    (swap! messages conj ^{:id id}
                     [(keyword (str "div.ui.message." (name type)))
                      [:i.close.icon {:on-click #(remove-message messages id)}]
                      (when header
                        [:div.header header])
                      content])))

(defn add-notification [type header content]
  (let [id (gensym)]
    (swap! notifications conj ^{:id id}
                         [(keyword (str "div.ui.compact.message." (name type)))
                          (when header
                            [:div.header header])
                          content])
    (js/setTimeout #(remove-message notifications id) 3000)))