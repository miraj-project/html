(def +project+ 'miraj/html)
(def +version+ "5.1.0-SNAPSHOT")

(set-env!
 :source-paths #{"edn"} ;; "src/test/clj"}
 :resource-paths #{"src/main/clj"} ;;; "resources"
 ;; :resource-paths #{"resources"}
 ;; :target-path "resources/public"
 ;; :asset-paths #{"resources/public"}

 :checkouts '[[miraj/co-dom                  "0.1.0-SNAPSHOT"]
              [miraj/html                    "5.1.0-SNAPSHOT"]
              [miraj/core                    "0.1.0-SNAPSHOT"]]

 :dependencies '[[org.clojure/clojure        RELEASE]
                 [org.clojure/clojurescript  "1.7.228"]
                 [miraj/boot-miraj           "0.1.0-SNAPSHOT" :scope "test"]

                 [miraj/core                 "0.1.0-SNAPSHOT"]
                 [miraj/html                 "5.1.0-SNAPSHOT"]
                 [miraj/co-dom               "0.1.0-SNAPSHOT"]
                 ;; [miraj/polymer "1.2.3-SNAPSHOT"]
                 ;; [miraj/dom "1.2.3-SNAPSHOT"]
                 ;; [miraj/iron "1.2.3-SNAPSHOT"]
                 ;; [miraj/paper "1.2.3-SNAPSHOT"]
                 ;; [miraj/platinum "1.2.3-SNAPSHOT"]
                 ;; [miraj/boot-miraj "0.1.0-SNAPSHOT"]
                 ;; [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 ;; [adzerk/boot-cljs-repl "0.3.0" :scope "test"]
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
      :version +version+}
 jar {:manifest {"root" "miraj"}})

(deftask build
  "build a component library"
  []
  (comp (miraj/compile :libraries true :debug true)
        ;; (miraj/compile :styles    true :debug true :keep true)
        (target)))

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

;; plain repl won't do, the target dir will not be on the classpath
(deftask dev
  "watch etc."
  []
  (set-env! :source-paths #(conj % "src/test/clj"))
  (comp (serve)
        (watch)
        (notify :audible true)
        ;; (refresh)
        (miraj/compile :libraries true :debug true)
        (miraj/compile :pages true :debug true :keep true)
        (target)))

(deftask install-local
  "Build and install a component library"
  []
  (comp (build)
        (pom)
        (jar)
        (install)))

(deftask monitor
  "watch etc."
  []
  (comp (watch)
        (notify :audible true)
        (miraj/compile :libraries true :verbose true)
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
