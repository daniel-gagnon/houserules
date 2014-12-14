(ns houserules.routes.app
            (:require [houserules.layout :as layout]
                      [compojure.core :refer :all]))

(defn app-page []
      (layout/render
        "app.html"))

(defroutes app-routes
  (GET "*" [] (app-page)))
