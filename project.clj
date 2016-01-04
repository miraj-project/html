(defproject miraj/html "5.1.0-SNAPSHOT"
  :description "miraj html convenience fns"
  :url "https://github.com/mobileink/miraj.html"
  :license {:name "EPL"
            :url "http://opensource.org/licenses/eclipse-1.0.php"}
  :source-paths ["src/main/clj"]
  :dependencies [[org.clojure/clojure "1.8.0-RC4"]
                 [potemkin "0.4.1"]
                 [miraj/markup "0.1.0-SNAPSHOT"]]
  :profiles {:dev {;;:prep-tasks ^:replace ["clean" "compile"]
                   :source-paths ["src/main/clj" "src/test/clj" "src/test/config" "dev"]
                   :resource-paths ["dev-resources/public"]
                   :dependencies [[polymer/paper "1.2.3-SNAPSHOT"]
                                  [polymer/iron "1.2.3-SNAPSHOT"]
                                  ;;[miraj/html "5.1.0-SNAPSHOT"]
                                  [org.clojure/tools.namespace "0.2.1"]
                                  [org.clojure/tools.logging "0.3.1"]
                                  [org.slf4j/slf4j-log4j12 "1.7.1"]
                                  [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                                     javax.jms/jms
                                                                     com.sun.jmdk/jmxtools
                                                                     com.sun.jmx/jmxri]]
                                  [ clj-logging-config "1.9.7"]]}
             :test {:resource-paths ["test/resources"]
                    :dependencies [[miraj "1.1.4-SNAPSHOT"]
                                   [polymer/paper "1.2.3-SNAPSHOT"]
                                   [miraj/html "5.1.0-SNAPSHOT"]]}}
  :repl-options {:port 4001})
