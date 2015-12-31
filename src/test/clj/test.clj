(ns test
  (:require ;;[miraj.markup :refer :all :exclude [normalize]]
            [miraj.html :as h]
            [clojure.tools.namespace.repl :refer [refresh]]
            ;; [polymer.paper]
            #_[clojure.data.xml :as x]))

;; (use 'miraj.markup :reload)
(use 'scripts :reload)
;; (use 'styles :reload)

(h/pprint
 (miraj.markup/normalize
  (miraj.markup/optimize :js
;;  (with-meta
    (h/html
     (h/require '[polymer.paper :as paper :refer [button]]
                '[polymer.iron :as iron :refer [icon pages]])
                ;; '[scripts :refer [jquery materialize]]
                ;; '[styles  :refer [foo bar]])
     (h/import #_(styles.shared.foo fooa foob)
               '(styles.shared foo bar)
               '(styles foo bar))
     (h/body (h/h1 "hello")
             (paper/button "foo")
             (iron/list)
             (iron/icon {:icon "menu"}))))))

(def homepage-html
  (h/html
   (h/head
    (h/script {:src "bower_components/webcomponentsjs/webcomponents-lite.js"})
    (h/platform {:apple {:mobile-web-app {:capable true
                                          :status-bar-style :black
                                          :title "Hello"}}
                 :ms {:navbutton-color "#FFCCAA"
                      :tile-color "#3372DF"
                      :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                 :mobile {:agent {:format :html5
                                  :url "http://example.org/"}
                          :web-app-capable true}})
    (h/link {:rel "stylesheet" :type "text/css" :href "styles/hello.css"})
    (h/link {:rel "import" :href "bower_components/paper-button/paper-button.html"})
    (h/link {:rel "import" :href "bower_components/paper-card/paper-card.html"})
    (h/script {:src "https://code.jquery.com/jquery-2.1.1.min.js"}))

   (h/body
    (h/h1 "Hello Polymer, with HTML primitives!")
    (h/div {:id "cards"}
           (paper/card {:heading "Card Title"}
                       (h/div {:class "card-content"} "Some content")
                       (h/div {:class "card-actions"}
                              (paper/button {:raised nil} "Some action")))))))

;;(def homepage
;;(println
  (->> homepage-html
       h/normalize
       (h/optimize :js)
       h/pprint)

(def home-meta
  ;;FIXME - no hardcoded values, always use indirection.
  ;; e.g. not :tile-color "#3372DF" but :tile-color :foo
  ;; ditto esp. for URIs
  ;; for this we need a config namespace, just like for js and css resources
  ;; for images, the schema will tell us which vals are supposed to be URIs,
  ;; so we will then look up the kw in the images namespace.  ?
  ;; ditto for colors - define a color namespace
  ;; what about literals? we should already use indirection, for i18n
  ;; or we could use a single meta namespace
  {:html-meta
   {:lang "en-us"
    :title "Demo webpage"
    :description "blah blah"
    :keywords "foo bar"
    :viewport {:width :device
               :scale {:initial "1.0" :min "1.0"}
               :user-scalable true}
    :platform {:apple {:mobile-web-app {:capable true
                                        :status-bar-style :black
                                        :title :hello-str} ;; "Hello"
                       :touch {:icon {:uri "/images/touch/chrome-touch-icon-192x192.png"
                                      :sizes "192x192"}}}
               :ms {:navbutton-color "#FFCCAA"
                    :tile-color "#3372DF"
                    :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
               :mobile {:agent {:format :html5
                                :url "http://example.org/"}
                        :web-app-capable true}}}})

(def home-html
  (h/html
   (h/require '[polymer.paper :as paper :refer [button card]]
              '[polymer.iron :as iron :refer [icon icons]])
   (h/import '(styles hello world)
             #_(html-imports hello world)
             #_(styles.shared foo bar)
             '(scripts polyfill jquery))
   ;; (h/meta {:name "charset" :content "ascii"})
   (h/body
    (h/h1 "Hello Polymer!")
    (h/div (iron/icon {:icon "menu"}))
    (h/div {:id "cards"}
           (paper/card {:heading "Hello, you ol' Card!"}
                       (h/div {:class "card-content"} "Some content")
                       (h/div {:class "card-actions"}
                              (paper/button {:raised nil} "Some action")))))))

(-> home-html
    (with-meta home-meta)
    h/normalize
    h/optimize
    h/pprint)

(meta (with-meta home-html home-meta))


    ;; {:title "hello" :description "foo" :base "http://foo"
    ;;  :platform
    ;;  {:apple {:touch
    ;;           {:icon "images/touch/apple-touch-icon.png"}}}})))

;; we support literal script and style elts:
;; (h/script {:src "foo.js"})
;; (h/style {:href "foo.css"})
;; but the recommended practice is to use h/require with a config
;; namespace.


;; <!-- import the shared styles  -->
;; <link rel="import" href="../shared-styles/shared-styles.html">
;; <!-- include the shared styles -->
;; <style is="custom-style" include="foo-style"></style>
;; <style is="custom-style" include="bar-style"></style>



;; (refresh)

;; (meta (with-meta (element :foo) {:a :b}))


;; scripts/jquery


;; (remove-ns 'polymer.paper)
;; (ns-unalias *ns* 'paper)

;; (paper/button)

;; (require '[polymer.paper :as paper :refer [button card]])

;; (paper/button)

;; (remove-ns 'polymer.iron)


;; (ns-unalias *ns* 'iron)
;; (create-ns 'polymer.iron)
;; (require '[polymer.iron :as iron :refer [icon list]])

;; (iron/label)

;; iron

;; (meta (find-ns 'polymer.iron))

;; polymer.iron/icon

;; (println (meta (find-var 'polymer.paper/button)))

;; (println (meta (find-var 'polymer.iron/icon)))

;; (println (meta (find-var 'clojure.core/list)))

;; ;;(refresh)

;; (def doc (element :html
;;                   (element :head
;;                            (element :meta {:name "description"
;;                                            :content "co-compile test"})
;;                            (element :link {:rel "stylesheet" :href "/scripts/foo.css"} "BUG!")
;;                            (element :script {:src "/scripts/foo.js"}))
;;                   (element :body
;;                            (element :h1 "Hello World"))))

;; doc

;; (println doc)

;; (pprint doc)

;; (println (serialize doc))

;; (println (optimize :js doc))

;; (pprint (optimize :js doc))

;; (co-compile "resources/footest.html"
;;             (optimize :js doc)
;;             :pprint)

;; (def x "foo")

;; (println (serialize (element :link {:rel "stylesheet" :href="foo.css"})))

;; (println (element :foo {:bar ""}))


;; ;;(x/emit-str (x/element :foo {:bar (* 2 3)}))
