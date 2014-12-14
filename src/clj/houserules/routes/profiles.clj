(ns houserules.routes.profiles
  (:require [compojure.core :refer :all]
            [houserules.profiles :refer [update-profile]]
            [noir.response :refer [edn]]))

(defroutes profile-routes
   (PUT "/profiles/update" request
        (update-profile (:params request))
        (edn true)))
