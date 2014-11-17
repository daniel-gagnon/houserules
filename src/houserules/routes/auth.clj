(ns houserules.routes.auth
  (:require [compojure.core :refer :all]
            [houserules.auth :refer [logout whoami admin? verify-token invalid-token? get-user]]
            [houserules.email :as email]
            [houserules.routes.app :refer [app-page]]
            [noir.response :refer [redirect edn]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))

(defroutes auth-routes
  (POST "/auth/login" [assertion]
        (let [email nil]
          {:body {:email email :admin (admin?)} :status (if email 200 403)}))
  (POST "/auth/logout" []
        (logout)
        (edn true))
  (GET "/auth/whoami" []
        (let [email (whoami)]
          (edn {:email email :admin? (admin?) :invalid-token? (invalid-token?)})))
  (POST "/auth/register" [email]
        (if-not (get-user email)
          (do
            (email/send-registration-email email)
            (edn true))
          (edn false)))
  (GET "/auth/xsrf" [] (edn *anti-forgery-token*))
  (GET "/register/:token" [token]
       (if (= (verify-token token) :already-registered)
         (redirect "/sign/in")
         (app-page))))