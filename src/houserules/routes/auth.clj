(ns houserules.routes.auth
  (:require [compojure.core :refer :all]
            [houserules.auth :refer [verify-assertion]]))

(defroutes auth-routes
  (POST "/auth/login" [assertion]
        (let [email (verify-assertion assertion)]
          {:body {:logged (boolean email) :email email} :status (if email 200 403)})))