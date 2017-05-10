;   Copyright (c) Gregg Reynolds. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "Test element handling"
      :author "Gregg Reynolds"}
  miraj.html.elts-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [miraj.html :as h]
            [miraj.co-dom :refer :all :exclude [import require]]))

(deftest ^:elts defelt
  (let [e (h/div)]
    (is (= e #miraj.co_dom.Element{:tag :div, :attrs {}, :content ()}))))

(deftest ^:elts defeltmeta
  (let [e (with-meta (h/div) {:bar :baz})]
    (is (= e #miraj.co_dom.Element{:tag :div, :attrs {}, :content ()}))
    (is (= (meta e) {:bar :baz}))))

(deftest ^:elts as-fragment
  (let [input (list [:tag1 "stuff"]
                    [:tag2 "other"])]
    (is (= (sexps-as-fragment input)
           (map sexp-as-element input)))
    (is (thrown? Exception (sexp-as-element input)))))

(deftest ^:elts elts-1
  (testing "ctor permutations"
    (is (= (h/div)
           #miraj.co_dom.Element{:tag :div, :attrs {}, :content ()}))
    (is (= (h/div {})
           #miraj.co_dom.Element{:tag :div, :attrs {}, :content ()}))
    (is (= (h/div {:class "bar"})
           #miraj.co_dom.Element{:tag :div, :attrs {:class "bar"}, :content ()}))
    (is (= (h/div "baz")
           #miraj.co_dom.Element{:tag :div, :attrs {}, :content ("baz")}))
    (is (= (h/div {} "baz")
           #miraj.co_dom.Element{:tag :div, :attrs {}, :content ("baz")}))
    (is (= (h/div {:class "bar"} "baz")
           #miraj.co_dom.Element{:tag :div, :attrs {:class "bar"}, :content ("baz")}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HTML element tests

;; (def html5-void-elt-tags
;;   #{"area" "base" "br" "col"
;;    "embed" "hr" "img" "input"
;;    "keygen" "link" "meta" "param"
;;    "source" "track" "wbr"})

(deftest ^:html void-1
    (is (= html5-void-elts
           #{"area" "base" "br" "col" "embed" "hr" "img" "input"
             "keygen" "link" "meta" "param" "source" "track" "wbr"})))

;; HTML5 void elements look just like standard elements in the node tree.
(deftest ^:html void-2
  (let [e (h/link)]
    (is (= e #miraj.co_dom.Element{:tag :link, :attrs {}, :content ()}))))

;; examples from https://developer.mozilla.org/en-US/docs/Web/HTML/Element/link
(deftest ^:html void-3
  (let [link0 (h/link {:href "style.css" :rel="stylesheet"})
        link1 (h/link {:href "default.css" :rel="stylesheet" :title "Default Style"})
        link2 (h/link {:href "fancy.css" :rel="alternate stylesheet" :title "Fancy"})
        link3 (h/link {:href "basic.css" :rel="alternate stylesheet" :title "Basic"})]
    (is (= link0 #miraj.co_dom.Element{:tag :link,
                                   :attrs {:href "style.css" :rel="stylesheet"},
                                   :content ()}))
    (is (= link1 #miraj.co_dom.Element{:tag :link,
                                   :attrs {:href "default.css" :rel="stylesheet" :title "Default Style"},
                                   :content ()}))
    (is (= link2 #miraj.co_dom.Element{:tag :link,
                                   :attrs {:href "fancy.css" :rel="alternate stylesheet"
                                           :title "Fancy"},
                                   :content ()}))
    (is (= link3 #miraj.co_dom.Element{:tag :link,
                                   :attrs {:href "basic.css" :rel="alternate stylesheet"
                                           :title "Basic"},
                                   :content ()}))))

;; HTML5 void elements serialization
(deftest ^:html void-4
  (testing "XML serialization of void element to empty, non-self-closing XML element"
    (let [link0 (h/link)
          link1 (h/link {:href "style.css" :rel="stylesheet"})]
      (is (= (serialize :xml link0)
             "<link></link>")))))

(deftest ^:html void-5
  (testing "HTML serialization of void element to unclosed start tag"
    (let [link0 (h/link)
          link1 (h/link {:href "style.css" :rel="stylesheet"})]
      (is (= (serialize :html link0)
             "<link>")))))

(deftest ^:html void-6
  (testing "Default serialization is :html"
    (let [link0 (h/link)
          link1 (h/link {:href "style.css" :rel="stylesheet"})]
      (is (= (serialize link0)
             "<link>")))))

(deftest ^:html void-7
  (testing "Serialization never generates self-closing tags."
    (let [link (h/link)
          div (h/div)]
      (is (= (serialize link) "<link>"))
      (is (= (serialize :html link) "<link>"))
      (is (= (serialize :xml link) "<link></link>"))
      (is (= (serialize div) "<div></div>"))
      (is (= (serialize :html div) "<div></div>"))
      (is (= (serialize :xml div) "<div></div>")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CDATA - <script> and <style>

(deftest ^:elts cdata-script
  (testing "CDATA - <script>"
    (is (= (serialize (h/script))
           "<script></script>"))
    (is (= (serialize (h/script "foo"))
           "<script>foo</script>"))
    (is (= (serialize (h/script "x < y"))
           "<script>x < y</script>"))
    (is (= (serialize (h/script "foo & bar"))
           "<script>foo & bar</script>"))
    (is (= "<script>1 + 1 > 1 + 2</script>"
           (serialize (h/script "1 + 1 > 1 + 2"))))
    (is (= "<script>1 + 1 < 1 + 2 && x < y</script>"
           (serialize (h/script "1 + 1 < 1 + 2 && x < y"))))
    (is (= "<script>
1 + 1 < 1 + 2 && x < y
</script>"
           (serialize (h/script "\n1 + 1 < 1 + 2 && x < y\n"))))))

(deftest ^:elts cdata-style
  (testing "CDATA - <style>"
    (is (= (serialize (h/style))
           "<style></style>"))
    (is (= (serialize (h/style "foo"))
           "<style>foo</style>"))
    (is (= (serialize (h/style "x < y"))
           "<style>x < y</style>"))
    (is (= (serialize (h/style "foo & bar"))
           "<style>foo & bar</style>"))
    (is (= (serialize (h/style "1 + 1 > 1 + 2"))
           "<style>1 + 1 > 1 + 2</style>"))
    (is (= (serialize (h/style "1 + 1 < 1 + 2 && x < y"))
           "<style>1 + 1 < 1 + 2 && x < y</style>"))))

(deftest ^:elts cdata-misc
  (testing "CDATA - clojurescript"
    (is (= "<script type=\"application/x-clojurescript\">(+ 1 2)</script>"
           (serialize (h/script {:type "application/x-clojurescript"} (+ 1 2)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; clojure vals
(deftest ^:elts clj-1
  (testing "clojure vals"
    (is (= "<span>3</span>"
           (serialize (h/span 3))))
    (is (= "<span>3</span>"
           (serialize (h/span (+ 1 2)))))))

(deftest ^:elts clj-data-1
  (testing "clojure data vals"
    (is (= "<span>123</span>"
           (serialize (h/span (map inc [1 2 3])))))
    (is (= "<span>123</span>"
           (serialize (h/span [1 :a "three"]))))
    (is (= "<span>1\n2</span>"
           (serialize (h/span '(1 2)))))))

