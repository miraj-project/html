(ns miraj.html
  (:refer-clojure :exclude [map meta time]) ;; apply fn
  (:require [clojure.spec :as spec]
            [clojure.string :as str]
            [clojure.tools.logging :as log :only [trace debug info warn error]]
            [miraj.co-dom :as codom]))

(alter-meta! *ns* (fn [m] (assoc m :miraj/miraj {:miraj/elements true
                                                 :miraj/nss '[]
                                                 :miraj/codom ""
                                                 :miraj/assets
                                                 {:miraj/bower
                                                  [

                                                   ]
                                                   :miraj/base ""}})))

(load "html_impl")
(load "html_spec")

;;;;;;;; COMPONENT: miraj.html/script ;;;;;;;;;;;;;;;;

;; attribs:
;; src;
;; type;
;; charset;
;; async;
;; defer;
;; crossOrigin;
;; text;

(defmacro script
  "Embedded script.

  https://www.w3.org/TR/html5/scripting-1.html#the-script-element"
  [& args]
  ;; (log/info (format "SCRIPT %s" args))
  ;; (log/info (format "NS %s" *ns*))
  (let [attrs (filter map? args)
        ;; _ (log/info "ATTRS:" attrs (empty? attrs))
        script-type (if (nil? (:type attrs))
                      {:type "application/x-clojurescript"}
                      {:type (:text attrs)})
        attribs (into {} (merge attrs script-type))
        ;; _ (log/info "ARGS:" args)
        content (if (empty? attrs)
                  args
                  (rest args))
        ;; _ (log/info "CONT:" content)
        content-str (str/join "\n" content)
        ]
    ;; (log/info (format "ATTRIBS: %s" attribs))
    ;; (log/info (format "CONTENT: %s" content-str))
    (apply codom/element :script attribs content)))

(alter-meta! (find-var (symbol (str *ns*) "script"))
             (fn [old new] (merge old new))
             {:miraj/miraj {:miraj/co-fn true
                            :miraj/element true
                            :miraj/html-tag :script
                            :miraj/lib :html
                            :miraj/assets {:miraj/href ""
                                           :miraj/bower ""}
                            :miraj/help "https://www.w3.org/TR/html5/scripting-1.html#the-script-element"}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn map->metas
  "Convert clojure map to sequence of HTML meta elements."
  {:added "5.1.0"}
  [html-tags]
  ;;(spec/check-asserts true)
  ;;(spec/assert ::meta html-tags)
  (let [parsed (spec/conform ::meta html-tags)]
    (if (= parsed ::spec/invalid)
      (log/warn (format "Invalid HTML meta-data map: %s" (spec/explain-str ::meta html-tags))))
    parsed)
  ;;(spec/check-asserts false)
  )


;; (defmacro apply
;;   "Like clojure.core/apply, but args must be clojurescript.  Use to specify inline event handlers."
;;   {:added "5.1.0"}
;;   [& sigs]
;;   )

;; (defmacro fn
;;   "Like clojure.core/fn, but defines a Clojurescript function, as an event handler."
;;   {:added "5.1.0", :special-form true,
;;    :forms '[(fn name? [params* ] exprs*) (fn name? ([params* ] exprs*)+)]}
;;   [& sigs]
;;   )
