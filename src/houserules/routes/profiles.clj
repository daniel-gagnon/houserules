(ns houserules.routes.profiles
  (:require [compojure.core :refer :all]
            [houserules.profiles :refer [update-profile]]))

(defroutes profile-routes
   (PUT "/profiles/update" [data]
        (update-profile data)
        (edn true)))
