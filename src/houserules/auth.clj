(ns houserules.auth
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn verify-assertion [assertion]
  (let [{:keys [status email]}
        (-> (http/post "https://verifier.login.persona.org/verify" {:form-params {:assertion assertion :audience "http://localhost:3000"}})
            deref
            :body
            (json/read-str :key-fn keyword))]
    (when (= status "okay") email)))