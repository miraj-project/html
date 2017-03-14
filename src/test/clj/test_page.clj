(ns test-page
  (:require [miraj.core :as miraj]
            [miraj.html.validate :as v]
            [miraj.co-dom :as codom]
            [miraj.html :as h]
            :reload))

;; (println "loading work.pages.minimal")

(miraj/defpage ^{:miraj/base "/" :miraj/demonstrates miraj.html}
  index
  "minimal example"
  #::h{:title "Miraj HTML lib test page"
       :description "this page demonstrates usage of miraj.html and polymer"}
  #::h{:platform {:apple {:mobile-web-app {:capable true
                                           :status-bar-style :black
                                           :title :goodbye-str} ;; "Goodbye"
                          :touch {:icon {:uri "/images/touch/chrome-touch-icon-192x192.png"}}}
                  :ms {:navbutton-color "#FFCCAA"
                       :tile-color "#3372DF"
                       :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                  :mobile {:agent {:format :html5
                                   :url "http://example.org/"}
                           :web-app-capable true}}}

  (:css "h1 {
    background-color: #009688; /* --paper-teal-500 */
    color: #E65100;} ;; --paper-orange-900

    div::first-letter {color: green; background-color: blue!important;}
")

  (:body
   (h/h1 "Minimal Demo")

   (h/div "i'm a foo div")
   (h/div
    (h/span {:foo 99
             :$background-color "#FFECB3"
             ;; :$before {:content "BEFORE_ "}
             ;;:$first-letter {:background-color "red"}
             ;; :$after {:content " _ AFTER"}
             :$hover {:background-color "#C5CAE9!important"}
             } "HELLO WORLD"))

   (h/div (h/span :.foo {:$before {:content "BYE: "}} "goodbye"))

   (h/style " .foo {color: blue}
    .foo:after {content: ' _After'}
")))

;; (miraj/normalize (var index))

;; index

;; (-> index var deref)
;;  :miraj/miraj ::h/meta)

;; (codom/pprint
;;   (miraj/normalize (var index))
;; )

;; (stencil.loader/set-cache (clojure.core.cache/ttl-cache-factory {} :ttl 0))

;; (binding [*compile-path* "tmp"]
;;   ;;miraj/*miraj-sym* (str (gensym "miraj"))]
;;   (wc/compile-page-nss #{(-> *ns* ns-name) 'work.pages.hello} true true))

;; (binding [*compile-path* "tmp"]
;;   (wc/link-pages #{(-> *ns* ns-name) 'work.pages.hello} true))


;;(println "loaded work.pages.minimal")

;; (def data-map
;;   #::h{:platform #::h{:apple #::h{:mobile-web-app {::h/status-bar-style :black
;;                                                   ::h/title "Goodbye"}
;;                                   :touch {::h/icon {::h/uri
;;                                                     "/images/touch/chrome-touch-icon-192x192.png"}}}
;;                       :ms {:navbutton-color "#FFCCAA"
;;                            :tile {:color "#3372DF"
;;                                   :image "images/ms-touch-icon-144x144-precomposed.png"}}
;;                       :mobile {:agent {:format :html5
;;                                        :url "http://example.org/"}
;;                                :web-app-capable true}}})

;; (require '[miraj.html :as h]
;;          '[miraj.html.validate :as v]:reload)

;; (miraj.html.validate/metas data-map)


;; (def dispatch-map
;;   #::h{:platform {:apple {:mobile-web-app {:status-bar-style :black
;;                                            :title :goodbye-str} ;; "Goodbye"
;;                           :touch {:icon {:uri "/images/touch/chrome-touch-icon-192x192.png"}}}
;;                   :ms {:navbutton-color "#FFCCAA"
;;                        :tile {:color "#3372DF"
;;                               :image "images/ms-touch-icon-144x144-precomposed.png"}}
;;                   :mobile {:agent {:format :html5
;;                                    :url "http://example.org/"}
;;                            :web-app-capable true}}})

;; (defn dispatch
;;   [data-map dispatch-map]
;;   (loop [data data-map dispatcher dispatch-map]
;;     (for [[k v] data-map]
;;       (if-let [action (get dispatch-map k)]
;;         (recur v action)
