(ns test-cljs
  (:require [clojure.pprint :as pp]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clojure.data.json :as json]
            [miraj.html :as h]
            [Polymer :refer :all]
            [Polymer.Behaviors]
            [Polymer.Events]
            [polymer.dom :as dom]
            [polymer.platinum :as plat]
            [polymer.platinum.service-worker :as sw]))

(defprotocol GP
  (^Bool foo [x] "FOO DOC")
  (bar [x] [x y]))

(pp/pprint GP)

;; prop ctor syntax:  (<pname> <default val> [boolean kws...]? <observer fn>?)
(use 'miraj.markup :reload)
(ns-unmap 'test 'TestP)

(class (t/date-time 1986 10 14 4 3 27 456))
(println (apply t/date-time [1986 10 14 4 3 27 456]))

(println (miraj.markup/cljs-compile '(cljs->js [1 2 3])))

(println (miraj.markup/cljs-compile '(fn [x y] (.log js/console "foo"))))

(println (miraj.markup/cljs-compile '(defn f [] (this-as this
                                                         (if x
                                                           (.log js/console "foo")
                                                           (.log js/console "bar"))))))

(miraj.markup/cljs-compile-str "js# [:a :b :c]")

(json/write-str [:a :b [:c :d]])
(json/write-str {:a 0 :b 1 :c [1 2 3]})
(json/write-str [:a "b" 0 {:x 99}])

  ;; multi-prop observers do not have a type annotation and take no flags:

(miraj.markup/defproperties MyProps
  (^Boolean president true :read-only)
  (^Number x 0 (fn [new old] (+ new old)) :notify :reflect)
  (^String lname "Lincoln" (fn [new old] (.log js/console
                                               (str "Old pres: " old "; new: " new)))))

MyProps

  (^Map amap {:a "b" 0 [:x]} :notify (fn [new old] (.log js/console "cljs here")))

(println (miraj.markup/props->cljs MyProps))

(println (miraj.markup/compile-props MyProps))

(pp/pprint MyProps)

(h/pprint
 (miraj.markup/<<!
  (miraj.markup/def-cotype MyComponent
    MyProps)))



  (^String fname "" (fn [new old] (.log js/console (str "old fname: " fname "; new fname: " fname))))
  (^String fullname :computed (fn [fname lname] (str fname " " lname)) :notify)
  (^String ^{:doc "myprop docstring"}
           myprop :computed (fn [x y] "foo") :notify )
  (multi-observer [fname lname y] (do (.log js/console "foo") "bar")))


  (^{:doc  "multi-observer docstr"}
   multi-observer [x y] "foo"))



  ;; computed properties:
  ;; FIXME: args to a computed prop must be props, just like multi-prop observer


  (^Vector avector [:a "b" 0 [:x]]))

 (^Date adate [1986 10 14 4 3 27 456] :notify))

  (^Number anumber 1 :notify (fn [x y] "...")))

  (^Number bnumber 1.0 :notify (fn [x y] "..."))
  (^Number dnumber 123e-5 :notify (fn [x y] "..."))
  (^Number cnumber 123e5 :notify (fn [x y] "...")))

  (^String astr "I'm a string" "and i'm a docstring"
           (fn [x y] "string observer"))
  (^Boolean abool true "docstring for abool" :notify))


  (^String foo "Welcome!" :notify :readonly
           (fn [x y] "foo observer here...")
           "foo docstring here"))

  ;; array property - default initializer must be a fn
  ;; unfortunately we have to mix clj and js, we can support cljs later:
  ;; https://www.polymer-project.org/1.0/docs/devguide/properties.html#array-observation
  (^Array users (fn [] "return [];") :notify "docstring for users property")
  ;; ditto for object properties:
  (^Object myobj (fn [] "return {};") :notify "docstring for myobj property")
  ;; NB: use  (fn [] "return {};") as default if type is Array or Object?


  ;; multiple dependencies
  ;; (https://www.polymer-project.org/1.0/docs/devguide/properties.html#change-callbacks)
  (fooBar [foo abool adate] "observer impl here..."))

(pp/pprint TestP)
