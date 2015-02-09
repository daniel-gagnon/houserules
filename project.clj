(defproject
  houserules
  "0.1.0-SNAPSHOT"
  :description
  "FIXME: write description"
  :url
  "http://example.com/FIXME"
  :source-paths ["src/clj" "src/cljs"]
  :dependencies
  [[reagent "0.4.3"]
   [reagent-forms "0.4.3"]
   [com.taoensso/tower "3.0.2"]
   [http-kit "2.1.19"]
   [prone "0.8.0"]
   [noir-exception "0.2.3"]
   [com.taoensso/timbre "3.3.1"]
   [selmer "0.8.0"]
   [cljs-ajax "0.3.9"]
   [lib-noir "0.9.5"]
   [org.clojure/clojurescript "0.0-2760"]
   [org.clojure/clojure "1.6.0"]
   [environ "1.0.0"]
   [ring-server "0.4.0"]
   [secretary "1.2.1"]
   [im.chit/cronj "1.4.3"]
   [org.clojure/data.json "0.2.5"]
   [com.sleepycat/je "6.2.7"]
   [com.taoensso/nippy "2.7.1" :exclusions [com.taoensso/encore org.clojure/tools.reader]]
   [slingshot "0.12.1"]
   [org.clojure/tools.trace "0.7.8"]
   [joda-time/joda-time "2.7"]
   [clj-yaml "0.4.0"]
   [markdown-clj "0.9.62" :exclusions [com.keminglabs/cljx]]
   [com.draines/postal "1.11.3"]
   [reagent-utils "0.1.1"]
   [org.clojure/clojurescript "0.0-2760" :scope "provided"]
   [com.cemerick/piggieback "0.1.5"]
   [weasel "0.5.0"]
   [figwheel "0.2.3-20150208.002609-4" :exclusions [org.clojure/core.async]]
   [leiningen "2.5.1"]]
  :repl-options
  {:init-ns houserules.repl}
  :jvm-opts
  ["-server"]
  :repositories [["Oracle" "http://download.oracle.com/maven/"]]
  :plugins
  [[lein-ring "0.8.13" :exclusions [org.clojure/clojure]]
   [lein-environ "1.0.0"]
   [lein-ancient "0.5.5" :exclusions [org.clojure/clojure org.clojure/clojure org.clojure/data.xml]]
   [lein-cljsbuild "1.0.3" :exclusions [org.clojure/clojure]]]
  :ring
  {:handler houserules.handler/app,
   :init houserules.handler/init,
   :destroy houserules.handler/destroy}
  :profiles {:dev {:repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.2"]
                                  [pjstadig/humane-test-output "0.6.0"]]

                   :plugins [[lein-figwheel "0.2.1-SNAPSHOT" :exclusions [org.clojure/clojure org.clojure/clojure org.clojure/clojurescript]]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :css-dirs ["resources/public/css"]}

                   :env {:dev? true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}}}

             :uberjar {:env {:production true}
                       :aot :all
                       :omit-source true
                       :hooks [leiningen.cljsbuild]
                       :cljsbuild {:jar true
                                   :builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                                           {:optimizations :advanced
                                                            :pretty-print false}}}}}

             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}}}
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src/cljs"],
     :compiler
     {:output-dir "resources/public/js/out",
      :externs ["react/externs/react.js" "externs/zxcvbn.js" "externs/recaptcha.js"],
      :optimizations :none,
      :output-to "resources/public/js/app.js",
      :source-map "resources/public/js/out.js.map",
      :pretty-print true}}}}
  :main
  houserules.core
  :min-lein-version "2.5.0")
