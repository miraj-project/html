(ns html
  (:require [miraj.html :as h]
            [miraj.html.pragma :as pragma]
            [miraj.co-dom :as xml]
            [miraj.polymer.dom :as dom]
            [clojure.tools.logging :as log :only [trace debug error info]]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [clojure.test]))

            ;; [miraj.markup.test-utils :refer [test-stream lazy-parse*]]))

;; ;; Polymer annotations: :foo => {{foo}} (two-way); 'foo => [[foo]] (one-way)
;; (xml/pprint :html (h/div {:items 'employees}))
;; (xml/pprint :html (h/div {:items :employees}))

(xml/pprint :html (h/span :index ", " :items))
(xml/pprint :html (h/span :index ", " 'items))
(xml/pprint :html (h/span 'index ", " :items))
(xml/pprint :html (h/span 'index ", " 'items))

(xml/pprint :html (h/span :foo " starts, then " :bar " is embeded, lastly: " :baz))
(xml/pprint :html (h/span :foo " starts, then " :bar " is embeded, lastly: " 'baz))
(xml/pprint :html (h/span :foo " starts, then " 'bar " is embeded, lastly: " :baz))
(xml/pprint :html (h/span :foo " starts, then " 'bar " is embeded, lastly: " 'baz))

(xml/pprint :html (h/span 'foo " starts, then " :bar " is embeded, lastly: " :baz))
(xml/pprint :html (h/span 'foo " starts, then " :bar " is embeded, lastly: " 'baz))
(xml/pprint :html (h/span 'foo " starts, then " 'bar " is embeded, lastly: " :baz))
(xml/pprint :html (h/span 'foo " starts, then " 'bar " is embeded, lastly: " 'baz))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; HTML boolean attribs: empty string or matching string, e.g. {:foo ""} or {:foo "foo"}
(xml/pprint :html (h/div {:foo "foo"} "Hello, world"))

(println (xml/serialize :html (h/div {:foo "foo"} "Hello 2, world")))

(xml/pprint :html (h/div {:foo "" :bar ""} "Hello 3, world"))

(println (xml/serialize :html (h/div {:foo ""} "Hello 4, world")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ID attrib:
(xml/pprint :html (h/span ::foo))
(xml/pprint :html (h/span ::foo "testing 123"))
(xml/pprint :html (h/span ::foo :index " testing 123 " 'items))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Class attrib:
(xml/pprint :html (h/span ::.foo))
(xml/pprint :html (h/span ::.foo.bar.baz))
(xml/pprint :html (h/span ::.foo "testing 123"))
(xml/pprint :html (h/span ::.foo.bar.baz :index " testing 123 " 'items))

;; ID and class
(xml/pprint :html (h/span ::foo.bar.baz))

;; nesting
(xml/pprint :html (h/div (h/span ::foo.bar.baz)))

;; repetition; it's just clojure
(xml/pprint :html (h/div
                   (repeat 3 (h/span ::.bar.baz "hi"))))

(xml/pprint :html (h/div
                   (for [i (range 1 4)]
                     (h/span ::.bar.baz (str "hi " i)))))

(xml/pprint :html (h/div
                   (for [i (range 1 4)]
                     (let [id (str "foo" i)]
                     (h/span (keyword (str *ns*) (str id ".bar.baz")) (str "hi " i))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; html void elements https://www.w3.org/TR/2011/WD-html-markup-20110525/syntax.html#void-element
;; "area" "base" "br" "col" "embed" "hr" "img" "input" "keygen" "link" "meta" "param" "source" "track" "wbr"
(xml/pprint :html (h/area))

(xml/pprint :html (h/link))

;;error: (xml/pprint :html (h/link "foo"))

;; <link href="default.css" rel="stylesheet" title="Default Style">
(xml/pprint :html (h/link {:href "default.css" :rel "stylesheet" :title "Default Style"}))

;; <link rel="stylesheet" href="mystylesheet.css" onload="sheetLoaded()" onerror="sheetError()">
(xml/pprint :html (h/link {:href "mystylesheet.css" :rel "stylesheet"
                           :onload "sheetLoaded()" :onerror "sheetError()"}))

;; <script>
;; function sheetLoaded() {
;;   // Do something interesting; the sheet has been loaded
;; }

;; function sheetError() {
;;   console.log("An error occurred loading the stylesheet!");
;; }
;; </script>
(xml/pprint :html (h/script "
function sheetLoaded() {
  // Do something interesting; the sheet has been loaded
}

function sheetError() {
  console.log(\"An error occurred loading the stylesheet!\");
}
"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; html null tags: "caption" "col" "colgroup" "head" "html" "legend" etc.

(xml/pprint :html (h/caption))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; pragma directives

(xml/pprint :html (pragma/content-type "utf-8"))

(xml/pprint :html (pragma/default-style "styles/default.css"))
(xml/pprint :html (pragma/refresh 300))
(xml/pprint :html (pragma/refresh "20; URL=page4.html"))
(xml/pprint :html (pragma/content-security-policy "script-src 'self'; object-src 'none'"))

(println (meta (var pragma/content-type)))

;; content attrib required
;; (xml/pprint :html (pragma/content-type))

;; meta elts must be empty
;; (xml/pprint :html (pragma/content-type "utf-8" "foobar"))

;; non-conforming, throws exception:
;; (xml/pprint :html (pragma/content-language "en"))
;; (xml/pprint :html (pragma/set-cookie "en"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; polymer dom stuff
(xml/pprint :html (dom/bind))
(xml/pprint :html (dom/if))
(xml/pprint :html (dom/for))
(xml/pprint :html (dom/module))
(xml/pprint :html (dom/content))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; html docs

;; print as html
(xml/pprint :html (h/html (h/body "foo")))

;; print as xml
(xml/pprint :xml (h/html (h/body "foo")))

(xml/pprint :html
            (h/html
             (h/head
              (h/link {:href "foo"}))
             (h/body
              (h/div
               (h/span "hello")))))


