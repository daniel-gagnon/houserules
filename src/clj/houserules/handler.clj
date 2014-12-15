(ns houserules.handler
  (:require [compojure.core :refer [defroutes]]
            [houserules.routes.app :refer [app-routes]]
            [houserules.routes.auth :refer [auth-routes]]
            [houserules.routes.profiles :refer [profile-routes]]
            [houserules.middleware :refer [load-middleware]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cronj.core :as cronj]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [houserules.database.bdb :refer [migrate shutdown-database db-get put with-transaction]]
            [houserules.settings :as settings]
            [clojure.java.io :as io]))

(defroutes static-routes
  (route/resources "/static/"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "houserules.log" :max-size (* 512 1024) :backlog 10})

  (if (env :dev?) (parser/cache-off!))

  (when-not (settings/read-settings)
    (println "E-mail settings could not be found. You won't be able to create your admin account without valid e-mail settings.")
    (settings/write-default-settings)
    (println "A default settings files as been created, please fill it in.")
    (System/exit 1))

  (migrate)

  (timbre/info "\n-=[ houserules started successfully"
               (when (env :dev?) "using the development profile") "]=-"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "houserules is shutting down...")

  (shutdown-database)

  (timbre/info "shutdown complete!"))

;; timeout sessions after 30 minutes
(def session-defaults
  {:timeout (* 60 30)
   :timeout-response (redirect "/")})

(defn- mk-defaults
       "set to true to enable XSS protection"
       [xss-protection?]
       (-> site-defaults
           (update-in [:session] merge session-defaults)
           (assoc-in [:security :anti-forgery] xss-protection?)))

(def app (app-handler
           ;; add your application routes here
           [auth-routes profile-routes static-routes app-routes]
           :session-options {:cookie-name "session"
                             :store (cookie-store (.getBytes settings/secret-key))}
           ;; add custom middleware here
           :middleware (load-middleware)
           :ring-defaults (mk-defaults true)
           ;; add access rules here
           :access-rules []
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn :transit-json]))
