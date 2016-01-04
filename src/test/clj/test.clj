(println "loading html test")
(ns test
  (:require [miraj.html :as h]
            ;; [clojure.tools.namespace.repl :refer [refresh]]
            [polymer.paper :as paper]
            #_[clojure.data.xml :as x]))

;;(use 'miraj.markup :reload)
;;(use 'miraj.html :reload)
;;(use 'polymer.paper :reload)
;;(use 'scripts :reload)
;;(use 'styles :reload)
;;(use 'styles.shared :reload)

(def homepage-html
  (h/html
   (h/head
    (h/script {:src "bower_components/webcomponentsjs/webcomponents-lite.js"})
    (h/meta-map
     {:title "hello.primitives demo"
      :description "this page demonstrates basic usage of miraj.html"
      :platform {:apple {:mobile-web-app {:capable true
                                                     :status-bar-style :black
                                                     :title "Hello"}}
                            :ms {:navbutton-color "#FFCCAA"
                                 :tile-color "#3372DF"
                                 :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                            :mobile {:agent {:format :html5
                                             :url "http://example.org/"}
                                     :web-app-capable true}}})
    (h/link {:rel "stylesheet" :type "text/css" :href "styles/hello.css"})

    (h/link {:rel "import" :href "bower_components/paper-button/paper-button.html"})
    (h/link {:rel "import" :href "bower_components/paper-card/paper-card.html"}))

   (h/body
    (h/h1 "Hello Polymer, with HTML primitives!")
    (h/div {:id "cards"}
           (paper/card {:heading "Card Title"}
                       (h/div {:class "card-content"} "Some content")
                       (h/div {:class "card-actions"}
                              (paper/button {:raised nil} "Some action")))))))

(h/pprint homepage-html)

(h/pprint
 (miraj.markup/optimize
   (miraj.markup/normalize
    (h/html
     (h/import '(scripts jquery materialize)
               '(styles foo bar materialize))
     (h/body (h/h1 "hello")
             (h/div "foo & bar")
             (h/style "div < foo{color:red;} ")
             (h/script "x < 7 & y > 3")
             (h/span "bar"))))))

;; (miraj.markup/co-compile
;;  "foo.html"
 (h/pprint
  (miraj.markup/optimize
   (miraj.markup/normalize
    (with-meta
      (h/html
       (h/import '(scripts jquery materialize))
       (h/body (h/h1 "hello")
               (h/div "foo & baz")
               (h/style "foo & bar")
               (h/script "foo < bar & 2 > 1")
               (h/div "foo & bar")))
      {:title "My co-compile"
       :description "desription here"
       :platform {:apple {:mobile-web-app {:capable true
                                           :status-bar-style :black
                                           :title "Hello"}}
                  :ms {:navbutton-color "#FFCCAA"
                       :tile-color "#3372DF"
                       :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                  :mobile {:agent {:format :html5
                                   :url "http://example.org/"}
                           :web-app-capable true}}}))))
   :pprint)


(h/pprint (h/script "foo"))

(h/pprint (h/script "blah " (+ 1 0) "<" (+ 1 2) " blahblah"))

(h/pprint (h/style 3))
(h/pprint (h/span (+ 1 3)))

(h/serialize (miraj.markup/element :link {:rel :stylesheet}))

(h/serialize (h/script {:class "a&b"} "a && b"))
(h/pprint (h/script {:class "a&b"} "a && b"))

(h/pprint (miraj.markup/element :span ::.a.b :a))

(h/pprint (h/span ::.a.b :a " & " :b))
(let [a "FOO"
      b "Bar"]
  (h/pprint (h/script ::.a.b a " & " b))
  (h/pprint (h/script ::.a.b 'a " & " b))
  (h/pprint (h/script ::.a.b :a " & " b))
  (h/pprint (h/style ::.a.b ":a & :b")))

(h/pprint (h/span '(1 2) :a))

(h/pprint (h/span ::.a.b :a))
(h/pprint (miraj.markup/element :span ::.a.b :a))

(h/pprint (h/span ::.foo.bar))
(h/pprint (miraj.markup/element :span ::.foo.bar))

(h/pprint (h/span ::.foo.bar 'a))
(h/pprint (miraj.markup/element :span ::.foo.bar 'a))

(h/pprint (h/span ::.foo.bar :a))
(h/pprint (miraj.markup/element :span ::.foo.bar :a))

(h/pprint (h/span ::foo))
(h/pprint (miraj.markup/element :span ::foo))

(h/pprint (h/span ::foo.bar.baz))
(h/pprint (miraj.markup/element :span ::foo.bar.baz))

(h/pprint (miraj.markup/element :span ::.a.b :a))
(h/pprint (h/span ::foo.a.b.c.d
                  "sadf asdf" :boozle "asdf asdef"))


(h/pprint
 (miraj.markup/optimize
  (miraj.markup/normalize
    (h/html
     (h/import '(scripts jquery materialize)
               '(styles foo bar materialize))
     (h/body
      (h/div ::main.col.s12.m4
             (for [x (range 1 3)]
               (h/ul {:id (str "x" x) :class "foo"}
                     (for [y (range 1 4)]
                       (h/li (str x "." y)))))))))))


(def homepage-html
  (h/html
   (h/head
    (h/meta {:name "viewport" :content "width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1, user-scalable=yes"})
    (h/meta-map {:platform {:apple {:mobile-web-app {:capable true
                                                 :status-bar-style :black
                                                 :title "Hello"}}
                        :ms {:navbutton-color "#FFCCAA"
                             :tile-color "#3372DF"
                             :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                        :mobile {:agent {:format :html5
                                         :url "http://example.org/"}
                                 :web-app-capable true}}})
    (h/link {:rel "stylesheet" :type "text/css" :href "styles/hello.css"})
    (h/script {:src "https://code.jquery.com/jquery-2.1.1.min.js"}))

   (h/body
    (h/h1 "Hello Miraj!")
    (h/div {:id "cards"}
           (h/div {:class "card" :heading "Card Title"}
                       (h/div {:class "card-content"} "Some content")
                       (h/div {:class "card-actions"}
                              (h/button {:raised nil} "Some action")))))))

;;(def homepage
;;(println
(->> homepage-html
     h/normalize
     (h/optimize :js)
     h/pprint)

(def home-meta
  ;;FIXME - no hardcoded values, always use indirection.
  {:html-meta
   {:lang "en-us"
    :title "Demo webpage"
    :description "blah blah"
    :keywords "foo bar"
    :viewport {:width :device
               :scale {:initial 1.0 :min 1.0 :max 1}
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
   (h/import '(styles foo bar)
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
