(ns houserules.routes.home
            (:require [houserules.layout :as layout]
                      [compojure.core :refer :all]))

(defn home-page []
      (layout/render
        "app.html"))

(defroutes home-routes
  (GET "/" [] (home-page)))
