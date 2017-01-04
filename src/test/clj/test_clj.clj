(ns test-clj
  (:require [clojure.pprint :as pp]
            [clj-time.core :as t]
            [clojure.data.json :as json]
            [miraj.html :as h]
            ;; [Miraj :refer :all]
            [miraj.polymer.Behaviors]
            [miraj.polymer.Events]
            [miraj.polymer.dom :as dom]
            ;; [miraj.paper :as paper]
            ;;[miraj.platinum :as plat]
            #_[miraj.platinum.service-worker :as sw]))

(def home-html
    (h/html
     (h/require '[miraj.paper :as paper :refer [button card]]
                '[miraj.iron :as iron :refer [icon icons]])
     (h/import '(styles hello world)
               '(scripts polyfill-lite-min))
     (h/body
      (h/h1 "Hello Miraj!")
      (h/div #_(iron/icon {:icon "menu"}))
      (h/div ::cards
             (paper/card {:heading "Hello, you ol' Card!"}
                         (h/div {:class "card-content"} "Some content")
                         (h/div {:class "card-actions"}
                                (paper/button {:raised nil} "Some action")))))))

(paper/card)

(println home-html)

(h/pprint home-html)

;; (remove-ns 'miraj.paper)

(h/pprint
(miraj.markup/def-codom greetings-dom
  "greetings-codom docstring"
  (h/require '[miraj.paper :as paper :refer [button card]]
             '[miraj.iron :as iron :refer [icon icons]])
  (h/import  '(styles.shared shared-styles))
  (h/style ":host {display: block;}")
  (h/h2 ::.page-title :greeting)
  (h/span ::.paper-font-body2 "Update text to change the greeting.")
  ;; Listens for "input" event and sets greeting to <input>.value
  (paper/card {:heading "Hello, you ol' Card!"}
                         (h/div {:class "card-content"} "Some content")
                         (h/div {:class "card-actions"}
                                (paper/button {:raised nil} "Some action")))
  (h/input {:class "paper-font-body2" :value :input->greeting})))

(meta (var greetings-dom))

greetings-dom

(h/pprint greetings-dom)

(miraj.markup/defproperties AProps
  ;; hostAttributes map
  {:string-attribute "Value"
   :boolean-attribute true
   :tabindex 0}
  (^Boolean president true :read-only)
  (^Number x 0 (fn [new old] (+ new old)) :notify :reflect)
  (^String lname "Lincoln" (fn [new old] (.log js/console
                                               (str "Old pres: " old "; new: " new)))))

(miraj.markup/defproperties BProps
  "BProps docstring here"
  (^Boolean bool-b true :read-only)
  (^Number ^{:doc "number y docstring"} y 99)
  (^String greeting "Hello Miraj!"))

(h/pprint
 (miraj.markup/<<!
  (h/def-cotype my-greeting
    "my-greeting custom component"
    ;; (codom ...)
    greetings-dom
    ;; (properties
    ;;  (^Boolean president true :read-only)
    ;;  (^Number x 0 (fn [new old] (+ new old)) :notify :reflect)
    ;;  (^String lname "Lincoln" (fn [new old] (.log js/console
    ;;                                               (str "Old pres: " old "; new: " new)))))
    AProps
    (lname "Washington")
    BProps

    Polymer.This
    ;; private
    (_foo [] (.log js/console "FOO"))
    ;; public
    (bar [] (.log js/console "BAR"))

    Polymer.Lifecycle
    (created [] (.log js/console "CREATED"))
    (attached [] (.log js/console "ATTACHED"))

    Polymer.Events.Gesture
    (with-element :special (tap [e] (.log js/console "you tapped the h1 element")))
    (down [x] (do (.log js/console "DOWN")))

    Polymer.Events.Mouse
    (click [x] (.log js/console "CLICK"))
    (dblclick [x] (.log js/console "DBLCLICK"))
    (mouseover [x] (.log js/console "MOUSEOVER"))

    Polymer.Behaviors.PaperButton
    Polymer.Behaviors.PaperCheckedElement
    )))

(defprotocol GP
  (^Bool foo [x] "FOO DOC")
  (bar [x] [x y]))

(pp/pprint GP)

(h/pprint
 (miraj.markup/<<!
  (miraj.markup/def-cotype my-greeting
    "my-greeting custom component"
    greetings-local-dom
    Greetings
    (greeting "Hi there!" (fn [old new] "console.log('greeting changed from ' old ' to ' new '!')"))
    (foo 9 :notify false)
    Polymer.Lifecycle
    (created [] "console.log('created ', this)")
    Polymer.Behaviors.PaperButton
    ;;(noink true) ;; => this.noink = true, in "create" handler?
    (method1 (fn [x] (+ 2 x)))
    )))

(my-greeting)

;; (miraj.markup/defpolymer my-foo
;;   "my docstr"
;;   (h/co-dom
;;    (h/require '[polymer.paper :as paper :refer [button card]])
;;    (h/p "hi world")))

(h/pprint
 (h/co-dom 'foo.bar
  (h/require '[polymer.paper :as paper :refer [button card]])
  (h/import  '(styles.shared foo bar))
  (h/style {:include "shared-styles"})
  (h/ul
   (h/template {:is "dom-repeat" :items "{{items}}"}
               (h/li (h/span {:class "paper-font-body1"}"{{item}}"))
               (h/li ::.special (h/span {:class "paper-font-body2"}"{{item}}"))
               (h/li (h/span {:class "paper-font-body3"}"{{item}}"))
               ))))

(h/pprint
 (h/optimize
  (h/normalize
 (->> (h/script  "x < " 3)
      (h/html (h/body (h/div
               (h/div
                (h/ul
                 (h/li "foo"))))))))))

(h/pprint
  (h/co-dom foobar
            (h/require '[polymer.paper :as paper :refer [button card]])
            (h/import  '(styles.shared foo bar))
            (h/style {:include "shared-styles"})
            (h/ul
             ;; (dom/for [item items]  ;; {:is "dom-repeat" :items "{{items}}"}
             (h/template {:is "dom-repeat" :items "{{items}}"}
                         (h/li (h/span ::.paper-font-body :item))
                         (h/li ::.special (h/span ::.paper-font-body2 :item))
                         (h/li (h/span ::.paper-font-body3 :item))))))

(ns-unmap 'test 'foo)

(defprotocol ^{:props '[foo baz]
               :resource-name "MyBehaviors.Foobar"
               :resource-type :polymer-behaviors
               :behaviors true
               :properties [:active :noink]
               :listeners true}
  Foobar
  (foo [this])
  (bar [this] [this x])
  (baz [this]))

(meta (var Foobar))

(miraj.markup/def-codom test-codom
  "test-codom docstring")

;;(^Map amap {:a "b" 0 [:x]} :notify (fn [new old] (.log js/console "cljs here")))

;; (println (miraj.markup/props->cljs MyProps))

;; (println (miraj.markup/compile-cljs MyProps))










{:behaviors {Polymer.Behaviors/PaperButton ((noink true) (method1 (fn [x] (+ 2 x))))
             Polymer.Behaviors/PaperCheckedElement ((noink true) (method1 (fn [x] (+ 2 x))))}
 }

(var my-greeting)

(miraj.markup/coprotocol? 'Polymer/Lifecycle)

(h/pprint
 (miraj.markup/<<! my-greeting))

my-greeting
(my-greeting)
(fn? my-greeting)
(var my-greeting)
(-> (meta (var my-greeting))
    :miraj
    :co-ctor)

(h/pprint
 (miraj.markup/<<! my-greeting))

    ;; Greetings
    ;; (greeting [] {:value "Hi there!"} "console.log('greeting changed!')")
    Polymer.Lifecycle
    (created [] "console.log('created ', this)"))))

(my-greeting)

(h/pprint
 (h/def-cotype my-list
   "my-list docstring"
  [^{:type Array, :notify true} items]
   (miraj.markup/codom
    (h/style ":host {display: block;}")
    (h/ul
     (h/template {:is "dom-repeat" :items :items}
                 (h/li (h/span ::.paper-font-body1 :item)))))
   Polymer.Lifecycle
   (ready [] "this.items = ['Responsive Web App boilerplate',
                           'Iron Elements and Paper Elements',
                           'End-to-end Build Tooling (including Vulcanize)',
                           'Unit testing with Web Component Tester',
                           'Routing with Page.js',
                           'Offline support with the Platinum Service Worker Elements'];")
   ;; ajax callbacks look just like any other event protocol method defns
   Polymer.Http
   (data ["url..."] {} "console.log('handling ajax result')")
   (ajax-response ["https://www.googleapis.com/youtube/v3/search"]
    {:get :json
     :part "snippet" :q "polymer"
     :auto nil})))
; or: we parameterize the protocol, which contains only 3 events:
Polymer.Ajax "https://www.googleapis.com/youtube/v3/search"
{:get :json :part "snippet" :q "polymer" :auto nil}
(response [:ajax-response] "console.log('ajax response received')")
(request [:ajax-request] "console.log('ajax request sent')")
(error [:ajax-error] "console.log('ajax error', ajax-error)")

;; we can defn an ajax spec just like a protocol, parameterized:
;; we have implicitly defprotocol Ajax, and defrequest extends this via parameterization
(defrequest MyRequest  ;; = deftype? no
  :indexes Http  ;; we could have other protocols, e.g. MQTT
  "https://www.googleapis.com/youtube/v3/search"
  {:get :json :part "snippet" :q "polymer" :auto nil})
;; Here Ajax is a family of protocols, MyRequest's params index into it.
;; defrequest looks like deftype with fields, but it isn't, it indexes into a family
;; ie. its a higher-order protocol

;; or:
(def youtube-channel (cochan Http "https://www.googleapis.com/youtube/v3/search"))
(def MyRequest (youtube-channel {:get :json :part "snippet" :q "polymer" :auto nil}))

;; then we can use a let form:
(h/let [ajax-reponse (h/observe MyRequest)]
  (h/for [item (:items ajax-response)] ...))

;; even better:
(h/for [item (h/<! MyRequest)]
  ...)

;; completely inline:
(h/for [item (h/<! (cochan Http "https://www.googleapis.com/youtube/v3/search"
                                {:get :json :part "snippet" :q "polymer" :auto nil}))]
  ...)


;; with ajax we have 2 things: http resp event, and binding of result to a var
;; so we split them out, using protocol defns for event obseration, and a let-form for binding
;; we want data bindings to be in situ, but observers can be anywhere

(h/pprint
(h/def-cotype my-greeting
  "my-greeting custom component"
  [^{:type String, :value "Welcome!", :notify true}
   greeting]
  (h/co-dom
   (h/import  '(styles.shared shared-styles))
   (h/style ":host {display: block;}")
   (h/h2 ::special.page-title :greeting)
   (h/span ::.paper-font-body2 "Update text to change the greeting.")
   ;; Listens for "input" event and sets greeting to <input>.value
   (h/input {:class "paper-font-body2" :value :input->greeting}))
  Polymer.Lifecycle
  (created [] "console.log('MY GREETINGS created!')")
  (attached [] "console.log('MY GREETINGS attached!')")
  Polymer.Events.Gesture
  (with-element :special (tap [] "console.log('you tapped the h1 element')"))
  (down [x] "console.log('DOWN'); console.log('AGAIN')")))

(println (meta (var my-greeting)))

(h/pprint
 (h/def-cotype foo-bar
            "doc string"
            [^{:type String, :value "Jones", :notify false, :read-only true,
               :reflect-to-attribute true}
             author
             ^{:type Number, :value 0}
             age]
            (h/co-dom
             (h/require '[polymer.paper :as paper :refer [button card]])
             (h/import  '(styles.shared foo bar))
             (h/style {:include "shared-styles"})
             (h/h1 ::special "Hullo")
             (h/ul
              ;; (dom/for [item items]  ;; {:is "dom-repeat" :items "{{items}}"}
              (h/template {:is "dom-repeat" :items "{{items}}"}
                          (h/li (h/span ::.paper-font-body :item))
                          (h/li ::.special (h/span ::.paper-font-body2 :item))
                          (h/li (h/span ::.paper-font-body3 :item)))))
            ;; what about internal helper fns?  do they need to be protocoled?
            ;; This ;; ?? a psuedo-protocol?
            ;; (defn _foo [x] )
            ;; (defn _foobar [x] )
            ;; ;; for exposed methods, use a protocol
            ;; Foobar
            ;; (foo (fn [x] (do (+ 1 3) (* 3 x))))
            ;; ;; implicit thunking?
            ;; (bar (fn [] (js/alert "Thank you for foobarring")))

            Polymer.Lifecycle
            (attached [a b] console.log("MY GREETINGS!"))
            Polymer.Events.Gesture
            (with-element :special (tap [b] alert("you tapped the h1 element")))
            (down [x] console.log("DOWN")\; console.log("AGAIN"))))


(meta (var foo-bar))

            ;; ;; Protocols
            ;; Polymer.Lifecycle
            ;; (created (fn [] (.log js/console('created ', this))))))

            ;; ;; ;; Behaviors
            ;; Polymer.Behaviors.PaperButton
            ;; ;;(noink true) ;; => this.noink = true, in "create" handler?
            ;; (method1 (fn [x] (+ 2 x)))

            ;; ;; Events
            ;; Polymer.Events.Gesture
            ;; (with-element :special (tap (fn [] (js/alert "you tapped the h1 element"))))
            ;; (down (fn [x]))

            ;; Polymer.Events.Mouse
            ;; (click (fn [x]))))


            ;; foo.bar/baz
            ;; Polymer.Behaviors/PaperCheckedElement
            ;; Polymer.Behaviors/IronButtonState

            ;; org.w3c.com.UIEvent
            ;; (load ...)
            ;; (unload ...)
            ;; org.w3c.com.Focus
            ;; (blur ...)
            ;; (focus ...)
            ;; (focusin ...)
            ;; (focusout ...)
            ;; org.w3c.com.MouseEvent
            ;; (click (fn [x]))
            ;; (dblclick ...)
            ;; (down ...)
            ;; (enter ...)
            ;; (leave ...)))

(var foo-bar)

(h/pprint (h/import '(styles.shared foo bar)))

(h/pprint (h/require '[polymer.paper :as paper :refer [button card]]))

(h/pprint (h/require '[foo.bar :as b :refer [x]]))

(b/x)

;;(h/pprint (h/require '[polymer.iron :as iron :refer [list]]))

(h/serialize (paper/button))

(meta (var paper/button))

(paper/button)

(paper/card {:foo 9} "baz")

(paper/fab)

(defn reset []
  (do
    (ns-unmap 'polymer.paper 'polymer.paper)
    (ns-unmap 'test 'foo)
    (remove-ns 'polymer.paper)
    (ns-unalias 'test 'paper)))
(reset)


;;(use 'miraj.markup :reload)
;;(use 'miraj.html :reload)
(use 'polymer.paper :reload)
;;(use 'scripts :reload)
;;(use 'styles :reload)
;;(use 'styles.shared :reload)
;;(use 'themes :reload)

(h/pprint
 (dom/module ::my-greeting
             (h/template
              (h/style {:include "shared-styles"})
              (h/style "
      :host {
        display: block;
      }"
                       ))))

(h/pprint
(h/html
 (h/link {:rel "import" :href "../../bower_components/polymer/polymer.html"})
  (dom/module ::my-greeting
              (h/template
               (h/style {:include "shared-styles"})
               (h/style "
      :host {
        display: block;
      }")

               (h/h2 ::.page-title :greeting)
               (h/span ::.paper-font-body2 "Update text to change the greeting.")
               ;; Listens for "input" event and sets greeting to <input>.value

               ;; FIXME: (h/input ::.paper-font-body2 {:value :input->greeting}))
               (h/input {:class "paper-font-body2" :value :input->greeting}))
              ;;better: put value of input event to greeting
               (h/input {:class "paper-font-body2" :input :value->greeting})
              ;; (h/input {:class "paper-font-body2" :value "{{greeting::input}}"}))

              (h/script "
    (function() {
      'use strict';

      Polymer({
        is: 'my-greeting',

        properties: {
          greeting: {
            type: String,
            value: 'Welcome!',
            notify: true
          }
        }
      });
    })(); "))))


(h/pprint
(h/html
  (h/link {:rel "import" :href "../../bower_components/polymer/polymer.html"})
  (dom/module ::my-list
  (h/template
    (h/style "
      :host {
        display: block;
      }
      ")
    (h/ul
      (h/template {:is "dom-repeat" :items :items}
        (h/li (h/span ::.paper-font-body1 :item)))))

  (h/script "
    (function() {
      'use strict';

      Polymer({
        is: 'my-list',
        properties: {
          items: {
            type: Array,
            notify: true,
          }
        },
        ready: function() {
          this.items = [
            'Responsive Web App boilerplate',
            'Iron Elements and Paper Elements',
            'End-to-end Build Tooling (including Vulcanize)',
            'Unit testing with Web Component Tester',
            'Routing with Page.js',
            'Offline support with the Platinum Service Worker Elements'
          ];
        }
      });
    })();
      "))))

 (h/h2 ::page-title 'greeting)
 (h/span ::.paper-font-body2 "Update text to change the greeting.")
 ;; Listens for "input" event and sets greeting to <input>.value
 (h/input ::.paper-font-body2 {:value "{{greeting::input}}"})))


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

(h/pprint
 (miraj.markup/optimize
   (miraj.markup/normalize
    (h/html
     (h/import '(styles.shared base)
               '(themes app))
     (h/body (h/h1 "hello"))))))

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

(h/pprint (h/span ::a.b.c "foo"))
(h/pprint (h/span ::.a.b.c "foo"))
(h/pprint (h/body ::.fullbleed.layout.vertical
                  {:unresolved nil}
                  (h/span "foo")))

(h/pprint (h/span ::.list {:attr-for-selected "data-route" :selected 'route}))


(h/pprint (h/a {:_href "{{baseUrl}}users/Chuck"} "Chuck"))


 (h/pprint (h/a {:data-route "users" :href "{{baseUrl}}users"}))

(h/serialize (h/span {:foo 'bar}))

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
