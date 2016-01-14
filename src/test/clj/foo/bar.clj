(ns foo.bar)


;;(def foo.bar {})

(defn x [] 99)

;; (co-dom
;;  (h/link {:rel "import" :href "polymer/polymer/polymer.html"})
;;  (h/link {:rel "import" :href "styles/shared/style-modules.html"})
;;  (h/dom-module  ;; ID = ctor string, added automatically
;;   (h/template
;;    (h/style {:include "shared-styles"})
;;    (h/ul
;;     (h/template {:is "dom-repeat" :items "{{items}}"}
;;                 (h/li (h/span {:class "paper-font-body1"}"{{item}}"))
;;                 (h/li {:id "special"} (h/span {:class "paper-font-body2"}"{{item}}"))
;;                 (h/li (h/span {:class "paper-font-body3"}"{{item}}"))
;;                   )))))

