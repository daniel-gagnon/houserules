(ns houserules.routes.auth
  (:require [compojure.core :refer :all]
            [houserules.auth :refer [verify-assertion logout whoami]]))

(defroutes auth-routes
  (POST "/auth/login" [assertion]
        (let [email (verify-assertion assertion)]
          {:body {:logged (boolean email) :email email} :status (if email 200 403)}))
  (POST "/auth/logout" []
        (logout)
        {:body {:logged false :email nil}})
  (GET "/auth/whoami" []
        (let [email (whoami)]
          {:body {:logged (boolean email) :email email}})))