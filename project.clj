(defproject miraj/html "5.1.0-SNAPSHOT"
  :description "miraj html convenience fns"
  :url "https://github.com/mobileink/miraj.html"
  :license {:name "EPL"
            :url "http://opensource.org/licenses/eclipse-1.0.php"}
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.8.0-RC4"]
                 [potemkin "0.4.1"]
                 [miraj/markup "0.1.0-SNAPSHOT"]]
  :repl-options {:port 4001})
