(ns miraj.html.test-script
  (:require [miraj.co-dom :as codom]
            [miraj.html :as h]
            [miraj.polymer :as p]
            :reload))

(codom/pprint
;;(codom/serialize
(h/html (h/body (h/script (defn f [x] (.log js/console x))
                          (def m {::foo ::bar})
                          #_(def x (clj->js {:foo 9}))))))

(codom/pprint
 (h/div (h/span '((+ 3 4)))))


(def s (h/head
        (h/script {:type "text/javascript"}
console.log('hello');
alert('hello');
)))

(codom/pprint s)

(-> s :content)

(codom/serialize s)

;; script attribs:
;; src
;; type  text/javascript, default: application/x-clojurescript
;; charset
;; async
;; defer
;; crossOrigin
;; text
