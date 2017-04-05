(def +project+ 'miraj/html)
(def +version+ "5.1.0-SNAPSHOT")

(set-env!
 :source-paths #{"edn"} ;; "src/test/clj"}
 :resource-paths #{"src/main/clj"} ;;; "resources"
 ;; :resource-paths #{"resources"}
 ;; :target-path "resources/public"
 ;; :asset-paths #{"resources/public"}

 :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}])

 :checkouts '[[miraj/co-dom                  "1.0.0-SNAPSHOT"]
;;              [miraj/core                    "0.1.0-SNAPSHOT"]
              ]

 :dependencies '[[miraj/boot-miraj           "0.1.0-SNAPSHOT" :scope "test"]

                 ;; [miraj               "0.1.0-SNAPSHOT"]
                 [miraj/co-dom               "1.0.0-SNAPSHOT"]
                 [miraj/polymer               "1.2.3-SNAPSHOT"]
                 ;; [miraj/core                 "0.1.0-SNAPSHOT"]

                 ;; [miraj/polymer "1.2.3-SNAPSHOT"]
                 ;; [miraj/dom "1.2.3-SNAPSHOT"]
                 ;; [miraj/iron "1.2.3-SNAPSHOT"]
                 ;; [miraj/paper "1.2.3-SNAPSHOT"]
                 ;; [miraj/platinum "1.2.3-SNAPSHOT"]
                 ;; [miraj/boot-miraj "0.1.0-SNAPSHOT"]
                 ;; [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 ;; [adzerk/boot-cljs-repl "0.3.0" :scope "test"]

                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [clj-logging-config "1.9.7"]

                 [adzerk/boot-reload "0.5.1" :scope "test"]
                 [pandeiro/boot-http "0.7.3"           :scope "test"]
                 ;; [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT"  :scope "test"]
                 ;; [com.cemerick/piggieback "0.2.1"  :scope "test"]
                 ;; [mount "0.1.10" :scope "test"]
                 ;; [weasel "0.7.0"  :scope "test"]
                 [samestep/boot-refresh "0.1.0"]
                 [adzerk/boot-test "1.0.7" :scope "test"]])

(require '[miraj.boot-miraj :as miraj]
         '[samestep.boot-refresh :refer [refresh]]
         '[adzerk.boot-reload :refer [reload]]
         '[pandeiro.boot-http :as http :refer :all]
         '[adzerk.boot-test :refer [test]])

(task-options!
 repl {:port 8080}
 pom {:project +project+
      :version +version+
      :description "Clojure HTML5 Functions"
      :url         "https://github.com/miraj-project/html"
      :scm         {:url "https://github.com/miraj-project/html.git"}
      :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}}
 jar {:manifest {"root" "miraj"}})

(deftask build
  "build a component library"
  []
  (comp (miraj/compile :libraries true :debug true)
        ;; (miraj/compile :styles    true :debug true :keep true)
        #_(target)))

(deftask demos
  "build component demos"
  []
  ;;(set-env! :source-paths #(conj % "demos/clj"))
  (comp
   ;; (build)
   (watch)
   (miraj/compile :pages true :debug true :keep true)
   ;; (miraj/link    :pages true :debug true) ;; :keep true)
   ;; (miraj/demo-page :debug true)
   ;; (cljs)
   ;; (boot/sift :to-resource #{#".*\.cljs\.edn"}) ;; keep main.cljs.edn, produced by (cljs)
   (target   :no-clean   true)))

(deftask dev
  "watch etc."
  []
  (comp (repl)
        (watch)
        (notify :audible true)
        (build)))

        ;; (pom)
        ;; (jar)
        ;; (install)))

;; (deftask dev
;;   "watch etc."
;;   []
;;   (set-env! :source-paths #(conj % "src/test/clj"))
;;   (comp (repl)
;;         (watch)
;;         (notify :audible true)
;;         ;; (refresh)
;;         (miraj/compile :libraries true :debug true)
;;                                         ;        (miraj/compile :pages true :debug true :keep true)
;;         (target)))

(deftask install-local
  "Build and install a component library"
  []
  (comp (build)
        (pom)
        (jar)
        (install)))

(deftask run-demos
  "compile, link, serve demos"
  []
  (comp
   (serve :dir "target")
   (demos)
   (watch :verbose true)
   (notify :audible true)
   (miraj/compile :pages true :debug true :keep true)
   ;; (miraj/link    :pages true :debug true) ;; :keep true)
   ;; (miraj/demo-page :debug true)
   ;; (cljs-repl)
   ;;(refresh)
   ;; (miraj.boot-miraj/compile :keep true :debug true :pages true)
   ;; (miraj.boot-miraj/link    :debug true :pages true)
   (reload) ;; this is not for dev
   ;; (target) ;; :no-clean true)
   ;; (cljs)
   (target :no-clean true)
   #_(wait)))
