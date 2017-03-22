(ns miraj.html.test-html
  (:require [miraj.co-dom :as codom]
            [miraj.html :as h]
            [miraj.polymer :as p]
            :reload))

(def s (h/script {:type "application/x-clojurescript"}
          (defn f [x] (.log js/console x))))

s

;; script attribs:
;; src
;; type  text/javascript, default: application/x-clojurescript
;; charset
;; async
;; defer
;; crossOrigin
;; text



(def html-meta
  #::h{:title "Miraj HTML lib test page"
       :description "this page demonstrates usage of miraj.html and polymer"
       :platform {:apple {:mobile-web-app {:capable true
                                           :status-bar-style :black
                                           :title :goodbye-str} ;; "Goodbye"
                          :touch {:icon {:uri "/images/touch/chrome-touch-icon-192x192.png"}}}
                  :ms {:navbutton-color "#FFCCAA"
                       :tile-color "#3372DF"
                       :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                  :mobile {:agent {:format :html5
                                   :url "http://example.org/"}
                           :web-app-capable true}}})

(h/map->metas html-meta)

;;  polymer property binding

(codom/serialize (h/div {:foo :bar} "hello"))

(codom/serialize (h/div {:foo :bar} :msg))

(codom/serialize
 (h/div {:foo ::h/bar})
)

;; attribute binding
(codom/serialize
 (h/div {'@foo :bar})
)

;; using clojure binding notation:

(codom/serialize
 (h/div ['@foo :bar] :#my-id.bar.baz {:attr1 99} "hello")
)


(codom/serialize
 (p/bind
  (h/div {:total :total})
  (h/div {:sum :total})))



;; polymer special kws
(codom/serialize (h/h1 :#foo.bar.baz!centered "hi"))

(def body
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
"))

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
