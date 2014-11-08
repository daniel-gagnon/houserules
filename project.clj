(defproject
  houserules
  "0.1.0-SNAPSHOT"
  :description
  "FIXME: write description"
  :url
  "http://example.com/FIXME"
  :dependencies
  [[reagent-forms "0.2.4"]
   [com.taoensso/tower "3.0.2"]
   [http-kit "2.1.19"]
   [prone "0.6.0"]
   [noir-exception "0.2.2"]
   [com.taoensso/timbre "3.3.1"]
   [selmer "0.7.2"]
   [cljs-ajax "0.3.3"]
   [lib-noir "0.9.4"]
   [org.clojure/clojurescript "0.0-2371"]
   [org.clojure/clojure "1.6.0"]
   [environ "1.0.0"]
   [ring-server "0.3.1"]
   [secretary "1.2.1"]
   [im.chit/cronj "1.4.3"]
   [org.clojure/data.json "0.2.5"]
   [com.sleepycat/je "6.2.7"]
   [com.taoensso/nippy "2.7.0"]
   [slingshot "0.12.1"]]
  :repl-options
  {:init-ns houserules.repl}
  :jvm-opts
  ["-server"]
  :repositories [["Oracle" "http://download.oracle.com/maven/"]]
  :plugins
  [[lein-ring "0.8.13"]
   [lein-environ "1.0.0"]
   [lein-ancient "0.5.5"]
   [lein-cljsbuild "1.0.3"]]
  :ring
  {:handler houserules.handler/app,
   :init houserules.handler/init,
   :destroy houserules.handler/destroy}
  :profiles
  {:uberjar
   {:cljsbuild
    {:jar true,
     :builds
     {:app
      {:source-paths ["env/prod/cljs"],
       :compiler {:optimizations :advanced, :pretty-print false}}}},
    :hooks [leiningen.cljsbuild],
    :omit-source true,
    :env {:production true},
    :aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}
    :global-vars {*assert* false}},
   :dev
   {:cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}},
    :dependencies
    [[ring-mock "0.1.5"]
     [ring/ring-devel "1.3.1"]
     [pjstadig/humane-test-output "0.6.0"]],
    :injections
    [(require 'pjstadig.humane-test-output)
     (pjstadig.humane-test-output/activate!)],
    :env {:dev true}}}
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src-cljs"],
     :compiler
     {:output-dir "resources/public/js/out",
      :externs ["react/externs/react.js" "externs/persona.js"],
      :optimizations :none,
      :output-to "resources/public/js/app.js",
      :source-map "resources/public/js/out.js.map",
      :pretty-print true}}}}
  :main
  houserules.core
  :min-lein-version "2.0.0")
