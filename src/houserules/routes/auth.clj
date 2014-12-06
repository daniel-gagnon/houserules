(ns houserules.routes.auth
  (:require [compojure.core :refer :all]
            [houserules.auth :refer [logout whoami admin? verify-token invalid-token? get-user verify-password valid-recaptcha?]]
            [houserules.email :as email]
            [houserules.routes.app :refer [app-page]]
            [noir.response :refer [redirect edn status]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [noir.session :as session]
            [houserules.settings :refer [recaptcha-sitekey]]))

(defroutes auth-routes
  (POST "/auth/login" [email password]
        (if-not (get-user email)
          (status 403 (edn {:error :user-doesn't-exist}))
          (if (verify-password email password)
            (do
              (session/put! :email email)
              (edn (assoc (get-user email) :email email :admin (admin?))))
            (status 403 (edn {:error :password-error})))))
  (POST "/auth/logout" []
        (logout)
        (edn true))
  (GET "/auth/whoami" []
        (let [email (whoami)]
          (edn {:email email :admin? (admin?) :invalid-token? (invalid-token?)})))
  (POST "/auth/register" [email recaptcha]
        (if-not (get-user email)
          (if (valid-recaptcha? recaptcha)
            (do
              (email/send-registration-email email)
              (edn true))
            (status 403 (edn "Invalid recaptcha")))
          (edn false)))
  (GET "/auth/xsrf" [] (edn *anti-forgery-token*))
  (GET "/register/:token" [token]
       (if (= (verify-token token) :already-registered)
         (redirect "/sign/in")
         (app-page)))
  (POST "/reset-password" [email]
        (if (get-user email)
          (do
            (email/send-password-reset-email email)
            (edn true))
          (status 403 (edn false))))
  (GET "/auth/recaptcha/sitekey" []
       (edn @recaptcha-sitekey)))