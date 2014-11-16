(ns houserules.email
  (:require [noir.util.crypt :refer [sha1-sign-hex]]
            [houserules.settings :refer [secret-key system host port username password domain]]
            [markdown.core :refer [md-to-html-string]]
            [clojure.java.io :as io]
            [postal.core :refer [send-message]])
  (:import [org.joda.time DateTime]))

(defn make-token [email]
  (let [tomorrow (-> (DateTime.) (.plusDays 1) (.getMillis))
        hex (sha1-sign-hex secret-key (str email tomorrow))]
    (str email \~ tomorrow \~ hex)))

(defn send-registration-email [email]
  (let [link (str @domain "/register/" (make-token email))
        smtp-auth {:host @host
                   :user @username
                   :port @port
                   :pass @password}
        message {:from @system
                 :to email
                 :subject "Please confirm your Houserules registration"
                 :body [{:type "text/html; charset=utf-8"
                         :content (-> (io/resource "email-templates/register.txt")
                                      slurp
                                      (.replace "{email}" email)
                                      (.replace "{link}" link)
                                      md-to-html-string)}]}]
    (send-message smtp-auth message)))