(ns houserules.user
  (:require [reagent.core :as reagent :refer [atom]]
            [houserules.login :as login]
            [ajax.core :refer [GET POST]]))

(def name (atom nil))
(def email (atom nil))

(add-watch login/logged-in? (gensym)
  (fn [_ _ _ status]
    (case status
      true (GET "/auth/whoami"
                :handler
                (fn [response]
                  (reset! name (response "name"))
                  (reset! email (response "email"))))
      false (do
              (reset! name nil)
              (reset! email nil)))))

