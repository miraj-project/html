(defproject miraj/html "5.1.0-SNAPSHOT"
  :description "miraj html convenience fns"
  :url "https://github.com/mobileink/miraj.html"
  :license {:name "EPL"
            :url "http://opensource.org/licenses/eclipse-1.0.php"}
  :source-paths ["src/main/clj" "src/test/config"]
  ;; :resource-paths ["resources/public"]
  :clean-targets ^{:protect false} [:target-path "resources/public/scripts/cljs/"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.11.0"]
                 [org.clojure/data.json "0.2.6"]
                 ;; [org.clojure/clojurescript "1.7.228"]
                 [potemkin "0.4.1"]
                 [miraj/markup "0.1.0-SNAPSHOT"]]
  :profiles {:dev {;;:prep-tasks ^:replace ["clean" "compile"]
                   :source-paths ["src/main/clj" "src/test/clj" "src/test/config" "dev"]
                   :resource-paths ["dev-resources/public"]
                   :dependencies [[miraj/polymer "1.2.3-SNAPSHOT"]
                                  [miraj/iron "1.2.3-SNAPSHOT"]
                                  [miraj/paper "1.2.3-SNAPSHOT"]
                                  [miraj/platinum "1.2.3-SNAPSHOT"]
                                  [org.clojure/clojure "1.8.0"]
                                  [org.clojure/clojurescript "1.7.228"]
                                  [org.clojure/tools.namespace "0.2.1"]
                                  [org.clojure/tools.logging "0.3.1"]
                                  [org.slf4j/slf4j-log4j12 "1.7.1"]
                                  [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                                     javax.jms/jms
                                                                     com.sun.jmdk/jmxtools
                                                                     com.sun.jmx/jmxri]]
                                  [ clj-logging-config "1.9.7"]]}
             :test {:resource-paths ["test/resources"]
                    :dependencies [;;[miraj "1.1.4-SNAPSHOT"]
                                   ;;[miraj/paper "1.2.3-SNAPSHOT"]
                                   ]}}
  :plugins [[lein-cljsbuild "1.1.2"]]
  ;;           [lein-figwheel "0.5.0-4"]]
  ;; :cljsbuild {
  ;;             :builds [{:id "dev"
  ;;                       :source-paths ["tmp"]
  ;;                       :figwheel true
  ;;                       :compiler {:output-to "resources/public/scripts/main.js"
  ;;                                  :output-dir "resources/public/scripts/cljs"
  ;;                                  :main "t.my-greeting"
  ;;                                  :asset-path "scripts/cljs"
  ;;                                  ;; :optimizations :whitespace
  ;;                                  :pretty-print true}}]})
  :repl-options {:port 4001})
