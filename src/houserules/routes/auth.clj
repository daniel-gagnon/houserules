(ns houserules.routes.auth
  (:require [compojure.core :refer :all]
            [houserules.auth :refer [logout whoami admin? verify-token]]
            [houserules.email :as email]
            [houserules.routes.app :refer [app-page]]))

(defroutes auth-routes
  (POST "/auth/login" [assertion]
        (let [email nil]
          {:body {:email email :admin (admin?)} :status (if email 200 403)}))
  (POST "/auth/logout" []
        (logout)
        {:body true})
  (GET "/auth/whoami" []
        (let [email (whoami)]
          {:body {:email email :admin (admin?)}}))
  (POST "/auth/register" [email]
        (email/send-registration-email email)
        {:body true})
  (GET "/register/:token" [token]
       (do
         (verify-token token)
         (app-page))))