(ns t.my-greeting
   (:require [clojure.browser.repl :as repl]))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

;; (enable-console-print!)

(defn foo [a b]
  (+ a b))

;; (let [_x258018 (fn [x] (.log js/console "FOO"))]
;;   (js/Polymer
;;    (clj->js
;;     {:hostAttributes
;;      {:string-attribute "Value", :boolean-attribute true, :tabindex 0},
;;      :properties
;;      {:president {:type js/Boolean, :value true, :readOnly true},
;;       :x
;;       {:type js/Number,
;;        :value 0,
;;        :notify true,
;;        :reflectToAttribute true,
;;        :observer _x258018},
;;       :lname {:type js/String, :value "Lincoln", :observer _lname258019},
;;       :bool-b {:type js/Boolean, :value true, :readOnly true},
;;       :y {:type js/Number, :value 99},
;;       :string-b {:type js/String, :value "foo"}}
;;      :_x258018 (fn [x] (.log js/console "FOO"))
;;      })))
