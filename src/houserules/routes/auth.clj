(ns houserules.routes.auth
  (:require [compojure.core :refer :all]
            [houserules.auth :refer [verify-assertion logout whoami admin?]]))

(defroutes auth-routes
  (POST "/auth/login" [assertion]
        (let [email (verify-assertion assertion)]
          {:body {:email email :admin (admin?)} :status (if email 200 403)}))
  (POST "/auth/logout" []
        (logout)
        {:body true})
  (GET "/auth/whoami" []
        (let [email (whoami)]
          {:body {:email email :admin (admin?)}})))