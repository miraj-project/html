(ns miraj.html
  (:refer-clojure :exclude [map meta time]) ;; apply fn
  (:require [clojure.spec   :as spec]
            [clojure.string :as str]
            [clojure.walk   :as walk]
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
                      ;; {:type "application/x-clojurescript"}
                      {:type "text/javascript"}
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
(def html-kw-specs
  (let [fspecs (filter #(= "miraj.html"
                           (-> % first namespace)) (s/registry))
        specs (set (clojure.core/map #(-> % first) fspecs))]
    specs))

(defn validate-meta-keys
  "Recursively validates html-meta map keys, removing invalid ones."
  {:added "5.1.0"}
  [m]
  (log/debug (format "VALIDATE-meta-keys %s" m))
  ;; (log/debug (format "HTML-KW-SPECS %s" html-kw-specs))
  (let [f (fn [[k v]] ;; (log/debug (format "VVVV k: %s v: %s" k v))
            (if (contains? html-kw-specs k)
              [k v]
                (do (log/warn (format "HTML Meta: ignoring unrecognized key %s" k))
                    (if (and (keyword? k) (nil? (namespace k)))
                      (log/warn "\t(Did you forget to namespace it with miraj.html?)"))
                    {})))]
            ;; (if (keyword? k)
            ;;   (if (= "miraj.html" (namespace k))
            ;;     [k v]
            ;;     (do (log/warn
            ;;          (format "HTML Meta: ignoring key %s with ns %s (only ns miraj.html supported)"
            ;;                  k (namespace k)))
            ;;         {}))
            ;;   (do
            ;;     (log/warn (format "HTML Meta: ignoring key %s (only kw keys supported)"
            ;;                       (if (symbol? k) k (if (seq? k) (seq k) k))))
            ;;     {})))]
    ;; only apply to maps
    (walk/postwalk (fn [x] ;; (log/debug (format "POSTWALKING %s %s" x (clojure.core/map? x)))
                     (if (clojure.core/map? x)
                       (into {} (clojure.core/map f x))
                       x))
                   m)))

(defn validate-html-meta
  [html-meta]
  (log/debug (format "Validate HTML meta %s" html-meta))
  (let [v-meta (validate-meta-keys html-meta)
        ;; _ (log/debug (format "Validated HTML meta: %s" v-meta))
        parsed (spec/conform ::meta v-meta)]
    (if (not (spec/valid? ::meta v-meta)) ;;= parsed ::spec/invalid)
      (do
        (log/warn (format "Invalid HTML meta-data map: %s" (spec/explain-str ::meta v-meta)))))
    v-meta))
;;      v-meta)))

(defn meta-map->elts
  "Convert clojure map to sequence of HTML meta elements."
  {:added "5.1.0"}
  [html-tags]
  (log/debug (format "META-MAP->ELTS %s" html-tags))
  ;; (log/debug (format "XFORM registry %s" @xform-registry))
  #_(log/debug (format "PREWALK %s" (with-out-str
                                    (walk/prewalk-demo html-tags))))
  (let [f (fn [[k v]]
            (log/debug (format "Xforming: %s %s" k (if (seq? v) (seq v) v)))
            (apply
               (get @xform-registry k
                    (fn [k v] (log/debug (format "NO HANDLER FOUND FOR %s" k))
                      []))
               [k v]))]
    (flatten (walk/postwalk (fn [x] ;; (log/debug (format "WALKING %s %s" x (clojure.core/map? x)))
                     (if (clojure.core/map? x)
                       (let [;; _ (log/debug (format "INPUT MAP %s" x))
                             res (remove empty? (into [] (clojure.core/map f x)))
                             ;; _ (log/debug (format "OUTPUT %s" (seq res)))
                             ]
                         res)
                       ;; (if (keyword? x)
                       ;;   (do (log/debug (format "KW NODE %s" x))
                       ;;       (apply
                       ;;        (get @xform-registry x
                       ;;             (fn [x] (log/debug (format "NO HANDLER FOUND FOR %s" x))
                       ;;               []))
                       ;;        [x]))
                         x))
                   html-tags))))
