(in-ns 'miraj.html)

(require '[clojure.spec.alpha :as s]
         '[clojure.string :as str]
         '[miraj.co-dom :as codom :refer [element co-dom-node?]]
         '[clojure.tools.logging :as log :only [trace debug info warn error]])

;; https://www.quotes.uk.com/web-design/meta-tags.php

(def xform-registry (atom {}))

(defn register-xform [k f]
  ;; (log/debug (format "REGISTER XFORM %s" k))
  (swap! xform-registry assoc k f))

(defn ->link
  [rel args-map]
  ;; (log/debug "LINK:" rel args-map)
  (let [attribs (if (string? args-map)
                  {:rel "stylesheet" :href args-map}
                  (if (map? (first args-map))
                    (apply merge {:rel (clojure.core/name rel)} args-map)))
        ;; _ (log/debug (format "ATTRS %s" attribs))
        elt (element :link attribs)]
    ;; (log/debug "Link:" elt)
    elt))

(defn ->meta
  [tag content]
  (element :meta {:name (clojure.core/name tag) :content content}))

(defn noop
  [tag content]
  ;;(log/debug (format "NOOP %s %s" tag (if (seq? content) (seq content) content)))
  {tag content})

;; types

;; comma-separated tokens, e.g. for ::keywords
(s/def ::tokens? (s/coll-of string?))

;; https://www.w3.org/TR/html5/document-metadata.html#standard-metadata-names
#_(def html5-meta-attribs-standard
  #:html{:charset :encoding-decl   ;; :content is implicit as value of map entry
         ;; standard vals for name attrib
         :application-name :string
         :author :string
         :description :string
         :generator :string
         :keywords :tokens})
(s/def ::charset string?)
(register-xform ::charset ->meta)

(s/def ::application-name string?)
(register-xform ::application-name ->meta)

;; see author link-type below
;; (s/def ::author string?)
;; (register-xform ::author ->meta)

(s/def ::description string?)
(register-xform ::description ->meta)

(s/def ::generator string?)
(register-xform ::generator ->meta)

(s/def ::keywords ::tokens?)

;; http://www.metatags.org/all_metatags

;; <meta name="rating" content="safe for kids">
;; general
;; mature
;; restricted
;; 14 years
;; safe for kids

;; <meta name="no-email-collection" content="link or terms">

;; Google
;; Meta tags that Google understands: https://support.google.com/webmasters/answer/79812?hl=en
;; https://webmasters.googleblog.com/2007/03/using-robots-meta-tag.html
;; https://developers.google.com/webmasters/control-crawl-index/docs/robots_meta_tag
;; <meta name="google" content="nositelinkssearchbox" />
;; <meta name="google" content="notranslate" />
;; <meta name="google-site-verification" content="..." />
;; <meta name="robots" content="noindex, nofollow">
;; <meta name="googlebot" content="noindex,nofollow"/>
;; robots content values: all (default), noindex, nofollow, noarchive,
;;          nosnippet, noodp, notranslate, noimageindex, unavailable_after: [RFC-850 date/time]
;;          none (= noindex, nofollow)

;; viewport: https://drafts.csswg.org/css-device-adapt/#viewport-meta
;; (s/def ::device-width (fn [x] (= x :device-width)))
;; (register-xform ::device-width (fn [x] "device-width"))

(s/def ::width (fn [x] (or (number? x) (= x :device-width))))
(register-xform ::width (fn [tag content]
                          (str "width=" (if (number? content) content "device-width"))))

(s/def ::device-height (fn [x] (= x :device-height)))
(register-xform ::device-height (fn [x] "device-height"))

(s/def ::height (s/alt :n number? :h ::device-height))
(register-xform ::height noop)

(s/def ::initial number?)
(register-xform ::initial (fn [k v] (str "initial-scale=" v)))

(s/def ::min number?)
(register-xform ::min (fn [k v] (str "minimum-scale=" v)))

(s/def ::max number?)
(register-xform ::max (fn [k v] (str "maximum-scale=" v)))

(s/def ::user-scalable boolean?)
(register-xform ::user-scalable (fn [k v] (str "user-scalable=" (if v "yes" "no"))))

;; (s/def ::target-density (fn [x] (= x :device))
;; (register-xform ::target-density (fn [k v] (str "target-densitydpi=" (if v "yes" "no"))))

(s/def ::scale (s/keys :opt [::initial ::min ::max]))
(register-xform ::scale (fn [k v]
                          ;;(log/debug (format "SCALE %s %s" k (seq v)))
                          (str/join ", " v)))

  ;; <meta name="viewport" content="width=device-width, minimum-scale=1.0, initial-scale=1, user-scalable=yes">
(s/def ::viewport (s/keys :opt [::width ::height ::scale ::user-scalable]))
(register-xform ::viewport (fn [k v]
                             ;;(log/debug (format "VIEWPORT %s %s" k (seq v)))
                             (element :meta {:content (str/join ", " v)
                                             :name "viewport"})))


;; html5-pseudo-meta-attribs
(s/def ::title string?)
(register-xform ::title (fn [k v] (element :title v)))

(s/def ::base string?)

(s/def ::href string?)
(register-xform ::href (fn [k v]
                         {:href v}))

(s/def ::name string?)
(register-xform ::name (fn [k v] {:name (str v)}))

(s/def ::type string?)
(register-xform ::type (fn [k v] {:type (str v)}))

(s/def ::hreflang string?)
(register-xform ::hreflang (fn [k v] {:hreflang (str v)}))

(s/def ::cross-origin (fn [x] (contains? #{:anonymous :use-credentials} x)))
(register-xform ::cross-origin (fn [k v] {:cross-origin (clojure.core/name v)}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;  LINK elements
;;

(s/def ::media string?)
(register-xform ::media (fn [k v] {:media v}))

(s/def ::referrerpolicy string?)
(register-xform ::referrerpolicy (fn [k v] {:referrerpolicy v}))

;; catch-all
;; https://gist.github.com/kevinSuttle/1997924
(s/def ::rel string?)
(s/def ::content string?)
(s/def ::link (s/keys :req [::rel] :opt [::content ::title]))
(s/def ::links (s/coll-of ::link :kind vector?))
(register-xform ::links (fn [k links]
                          ;; (log/debug (format "LINKS %s" links))
                          (for [link links]
                            (let [;; _ (log/debug (format "LINK %s" (seq link)))
                                  m (for [l link]
                                      (do ;;(log/debug "FOO:" l)
                                            (if (co-dom-node? l)
                                              {::title (:content l)}
                                              l)))
                                  m (into {} m)
                                  ;; _ (log/debug (format "M %s" (seq m)))
                                  m (merge {:rel (::rel m)}
                                           (dissoc (if (::title m)
                                                     (dissoc (assoc m :title (::title m)) ::title)
                                                     m)
                                                   ::rel))]
                              ;; (log/debug (format "M2 %s" m))
                              (do ;; (log/debug (format "LINK %s" m))
                                (element :link m))))))

;; https://www.w3.org/TR/html5/links.html
;; ALTERNATE https://www.w3.org/TR/html5/links.html#rel-alternate
;; see also stylesheet
(s/def ::alternate (s/keys :req [::href] :opt [::type ::hreflang]))
(register-xform ::alternate (fn [k v]
                              (log/debug "ALTERNATE:" k v)
                              (element :link (apply merge {:rel "alternate"}
                                                    v))))

;; AUTHOR - we combine meta and link
;; https://dev.w3.org/html5/spec-preview/the-meta-element.html#standard-metadata-names
;; https://www.w3.org/TR/html5/links.html#link-type-author
(s/def ::author (s/keys :opt [::name ::href]))
(register-xform ::author (fn [k v]
                           (let [attrs (apply merge v)]
                             ;;(log/debug (format "AUTHOR %s" attrs))
                             (list
                              (if (:href attrs)
                                (element :link {:rel "author"
                                                :href (:href attrs)}))
                              (if (:name attrs)
                                (element :meta {:name "author" :content (str (:name attrs))}))))))

;; HELP  https://www.w3.org/TR/html5/links.html#link-type-help
(s/def ::help (s/keys :req [::href]))
(register-xform ::help ->link)

;; ICON  https://www.w3.org/TR/html5/links.html#rel-icon
(s/def ::size (s/cat :w number? :h number?))
(register-xform ::size (fn [k v] ;;(log/debug (format "SIZE %s %s" k v))
                         ))

(s/def ::sizes (s/or :v (s/coll-of ::size :kind set?) :f (fn [x] (= :any x))))
(register-xform ::sizes (fn [k v]
                          ;; (log/debug (format "SIZES %s %s" k v))
                          (if (= :any v)
                            {:sizes "any"}
                            {:sizes (str/join " " (for [[w h] v] (str w "x" h)))})))

(s/def ::icon (s/keys :req [::href]
                      :opt [::sizes ::type]))
(s/def ::icons (s/coll-of ::icon :kind vector?))
(register-xform ::icons (fn [k v] ;;(log/debug (format "ICONS %s %s" k v))
                          (for [icon v]
                            (do
                              ;; (log/debug (format "ICON %s" (into {} icon)))
                                        ;(let [attrs (for [attr v] (str
                              (element :link (merge {:rel "icon"}
                                                      (into {} icon)))))))

;; LICENSE https://www.w3.org/TR/html5/links.html#link-type-license
;; (s/def ::license (fn [x] (or (string? x) (and (map? x) (contains? #{::href} (keys x))))))
(s/def ::license (fn [x] (or (string? x)
                             (and (map? x) (every? #(contains? #{::href ::type} %) (keys x))))))
(register-xform ::license (fn [k v]
                            ;;(log/debug (format "LICENSE %s" (seq v)))
                            (element :link (apply merge {:rel "licence"}
                                                  (if (string? v)
                                                    {:href v}
                                                    v)))))

;; PREFETCH https://www.w3.org/TR/html5/links.html#link-type-prefetch
;;(s/def ::prefetch (s/keys :req [::name ::href]))
(s/def ::prefetch (fn [x] (or (string? x)
                              (and (map? x) (every? #(contains? #{::href ::type} %) (keys x))))))
(register-xform ::prefetch (fn [k v]
                            ;;(log/debug (format "PREFETCH %s" (seq v)))
                            (element :link (apply merge {:rel "prefetch"}
                                                  (if (string? v)
                                                    {:href v}
                                                    v)))))

;; SEARCH https://www.w3.org/TR/html5/links.html#link-type-search
;; (s/def ::search (s/keys :req [::name ::href]))
(s/def ::search (fn [x] (or (string? x) (and (map? x) (every? #(contains? #{::href ::type} %) (keys x))))))
(register-xform ::search (fn [k v]
                            ;;(log/debug (format "SEARCH %s" (seq v)))
                            (element :link (apply merge {:rel "search"}
                                                  (if (string? v)
                                                    {:href v}
                                                    v)))))

;; STYLESHEET(S)  https://www.w3.org/TR/html5/links.html#link-type-stylesheet
(s/def ::label string?)
(register-xform ::label (fn [k v] {:title v}))

(s/def ::stylesheet (s/keys :req [::href] :opt [::cross-origin]))
(register-xform ::stylesheet (fn [k v]
                               (log/debug (format "STYLESHEET %s %s" k v))
                               {k v}))

(s/def ::persistent (s/coll-of ::stylesheet))
(register-xform ::persistent (fn [k v]
                               ;; (log/debug (format "PERSISTENT %s %s" k (seq v)))
                               (for [ss v]
                                 (do
                                 ;; (log/debug (format "SS %s" (seq ss)))
                                 (element :link (apply merge {:rel "stylesheet"}
                                                       ss))))))

(s/def ::preferred (s/keys :req [::label ::href] :opt [::cross-origin]))
(register-xform ::preferred (fn [k v]
                              (let [attrs (apply merge v)
                                    ;; _ (log/debug (format "PREFERRED %s %s" k attrs))
                                    attribs (merge {:rel "stylesheet"} attrs)
                                    ;; _ (log/debug (format "PATRS %s" attribs))
                                    ]
                                (element :link attribs))))

;; (defn alt? [x] (and (map? x)
;;                     (every? #(contains? #{::href ::title} %) (keys x))))
(s/def ::alt-ss (s/keys :req [::href ::title]))
                       ;; (fn [x] (every? #(contains? #{::href ::title} %) (keys x)))))
(s/def ::alternates (s/coll-of ::alt-ss :kind vector? :distinct true))
(register-xform ::alternates (fn [k v]
                               ;; (log/debug (format "ALTERNATES %s %s" k v))
                               (for [ss v]
                                 (let [m (into {} ss)
                                       _ (log/debug (format "SS %s" m))
                                       m (dissoc m ::rel :attrs :tag)]
                                       (element :link (merge {:rel "alternate stylesheet"
                                                       :href (:href m)
                                                       ;; ::title has already been converted to an Element
                                                       :title (:content m)}
                                                             m))))))

(s/def ::stylesheets (s/keys :opt [::persistent ::preferred ::alternates]))
(register-xform ::stylesheets (fn [k v]
                                (log/debug (format "STYLESHEETS %s" (seq v)))
                                v))
                                ;; (element :link {:rel "stylesheet"})))

;; PAGINATION  https://www.w3.org/TR/html5/links.html#sequential-link-types
(s/def ::prev string?)
(register-xform ::prev (fn [k v] {:prev v}))

(s/def ::next string?)
(register-xform ::next (fn [k v] {:next v}))

(s/def ::pagination (s/keys :opt [::prev ::next]))
(register-xform ::pagination (fn [k v]
                               (for [m v]
                                 (if (:prev m)
                                   (element :link {:rel "prev" :content (:prev m)})
                                   (if (:next m)
                                     (element :link {:rel "next" :content (:next m)}))))))

(s/def ::content-security-policy string?)
(register-xform ::content-security-policy (fn [k v] {:Content-Security-Policy v}))

(s/def ::default-style string?)
(register-xform ::default-style (fn [k v] {k v}))

(s/def ::refresh string?)
(register-xform ::refresh (fn [k v] {k v}))

;; extensions
(s/def ::x-ua-compatible string?)
(register-xform ::x-ua-compatible (fn [k v] {:X-UA-Compatible v}))

(s/def ::pics-label string?)
(register-xform ::pics-label (fn [k v] {:PICS-Label v}))

(s/def ::pragma (s/keys :opt [::content-security-policy ::default-style ::refresh
                              ::pics-label]))
(register-xform ::pragma (fn [k v]
                           ;; (log/debug (format "PRAGMA %s" (into {} v)))
                           (for [[k v] (into {} v)]
                             (do ;; (log/debug (format "Pragma %s %s" k v))
                                 (element :meta {:http-equiv (clojure.core/name k)
                                                   :content v})))))

(s/def ::uri uri?)


;; mobile
;; <meta name="HandheldFriendly" content="true"/>
;; <meta name="MobileOptimized" content="320"/>  (windows proprietary?)

;; (s/def ::status-bar-style (fn [x]
;;                             (contains? #{:default :black :black-translucent} x)))

;; (register-xform ::status-bar-style (fn [k v] {k (clojure.core/name v)}))

;; (s/def ::mobile (s/and (s/keys :opt [::status-bar-style ::title])
;;                        (fn [x] (every? #(contains? #{::status-bar-style ::title} %) (keys x)))))
;; (register-xform ::mobile (fn [k v]
;;                            (log/debug (format "MOBILE %s" (seq v)))
;;                            (let [mob (for [elt v]
;;                                        (do (log/debug "MOBELT:" elt (co-dom-node? elt))
;;                                            (if (co-dom-node? elt)
;;                                              {::title (first (:content elt))}
;;                                              elt)))
;;                                  res {::mobile (into {::web-app-capable "yes"} mob)}]
;;                              (log/debug (format "MOBILE RES %s" (seq res)))
;;                              res)))

(s/def ::telephone-number-detection (fn [x] (= :disable)))

(s/def ::app (s/keys :opt [::id ::argument]))

(s/def ::itunes-app (s/keys :opt [::app ::affiliate-data]))

;; chrome
;; <meta name="mobile-web-app-capable" content="yes">
;; <link rel="shortcut icon" sizes="196x196" href="icon-196.png">
;; (s/def ::mobile-web-app (s/keys :opt [::status-bar-style ::title]))


;; android?
;; <link rel="apple-touch-icon-precomposed" href="/static/images/identity/HTML5_Badge_64.png" />


;; (def mobile-meta-tags
;;   {:mobile {:agent ^:compound {:format #{:wml :xhtml :html5}
;;                                :url :_}
;;             :web-app {:capability :fullscreen}}})

(s/def ::platform (s/keys :opt [::apple ::mobile ::ms]))
 ;; (fn [x]
 ;;                    ;; (log/debug (format "PLATFORM pred" ))
 ;;                    (and (map? x) (every? #(contains? #{::apple ::mobile ::ns} %) (keys x)))))

(register-xform ::platform (fn [k v]
                             (log/debug (format "PLATFORM %s" (seq v)))
                             v))

(s/def ::meta (s/keys :opt [::title ::description ::platform]))
(register-xform ::meta (fn [k v] v))


;; https://developer.apple.com/library/content/documentation/AppleApplications/Reference/SafariWebContent/PromotingAppswithAppBanners/PromotingAppswithAppBanners.html

#_(def twitter-meta-tags
  )

#_(def html5-meta-attribs-extended
   ;; extended name attribs https://wiki.whatwg.org/wiki/MetaExtensions
  {:viewport ^:compound {:width #{:auto :device :pixels}
                         :height #{:auto :device :pixels}
                         :scale {:initial :number
                                 :minimum :number
                                 :maximum :number}
                         :user-scalable :boolean}
   ;; dublin core
   :dc {:created :_, :creator :_} ;; etc.
   :dc-terms {:created :_, :creator :_} ;; etc.
   :fragment "!"
   :geo {:position :geocoord, :country :iso3166-1} ;; etc.
   :referrer #{:no-referrer :no-referrer-when-downgrade
               :origin :origin-when-cross-origin
               :unsafe-url}
   :revision :_
   :theme-color :color
   :twitter {:card :_
             :domain :_
             :url :_
             :title :_
             :description :_
             ;; etc.
             }})

#_(def html5-meta-attribs
  (merge {} html5-global-attrs
         html5-meta-attribs-standard
         html5-meta-attribs-extended
         html5-pseudo-meta-attribs
         html5-miraj-meta-attribs
         html5-link-meta))
         ;; apple-meta-tags ms-meta-tags))

#_(defn do-viewport
  [tag key val ruleset]
      ;; :viewport {:width :device
      ;;        :scale {:initial "1.0" :min "1.0"}
      ;;        :user-scalable true}
  ;; (println "do-viewport: " tag " | " key "|" val "|" ruleset)
  (let [w (:width val)
        width (str "width=" (if (= :device w) "device-width" w))
        initial-scale (str "initial-scale=" (get-in val [:scale :initial]))
        min-scale (str "minimum-scale=" (get-in val [:scale :min]))
        max-scale (str "maximum-scale=" (get-in val [:scale :max]))
        user-scalable (str "user-scalable=" (if (:user-scalable val) "yes" "no"))
        content (str/join ", " [width initial-scale min-scale max-scale user-scalable])]
    (element :meta {:name "viewport" :content content})))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  handle HTML meta elements
;;  http://w3c.github.io/html/document-metadata.html#the-meta-element

;; TODO: migrate to separate miraj.html/meta library, like miraj.html/pragma

#_(defn apply-meta-rule
  [tag key val ruleset]
  ;; (println (str "APPLY META RULE: " tag " | " key " | " val " | " ruleset))
  (condp = key
    :viewport (do-viewport tag key val ruleset)
    (let [this-tag (if (= :ms key) "msapplication" (subs (str key) 1))
          result (for [[k v] val]
                   (do ;;(println "key: " k ", val: " v)
                       (if-let [rule (get ruleset k)]
                         (let [k-tag (subs (str k) 1)]
                           ;; (println "rule: " rule)
                           (cond
                             (keyword? rule)
                             (do ;;(println "meta keyword rule: " k ", " val ": " rule)
                                 (let [val-param (get val k)
                                       nm (if (= "platform-" tag)
                                            (str this-tag "-" k-tag)
                                            (str tag this-tag "-" k-tag))
                                       elt (condp = rule
                                             :color (element :meta {:name nm
                                                                    :content (str val-param)})
                                             :boolean (element :meta {:name nm
                                                                      :content (str val-param)})
                                             :number (element :meta {:name nm
                                                                     :content (str val-param)})
                                             :pixels (element :meta {:name nm
                                                                     :content (str val-param)})
                                             :string (element :meta {:name nm
                                                                     :content (str val-param)})
                                             :sizes (element :meta {:name nm
                                                                    :content (str val-param)})
                                             :uri (element :meta {:name nm
                                                                  :content (str val-param)})
                                             :_ (element :meta {:name nm
                                                                :content (str val-param)})
                                             ;; :tokens
                                             )]
                                   ;;(log/trace "elt: " elt)
                                   elt))

                             (map? rule) (do ;;(println "meta map key: " k)
                                             ;; (println "meta map val: " (get val k))
                                             ;; (println "rule: " rule)
                                             ;; (println "tag: " tag)
                                             ;; (println "this-tag: " this-tag)
                                             (if (:compound (clojure.core/meta rule))
                                               (do
                                                   (let [nm (if (= "platform-" tag)
                                                              (str this-tag "-" k-tag)
                                                              (str tag this-tag "-" k-tag))
                                                         content (str/join
                                                                  "; " (for [[k v] (get val k)]
                                                                         (str (subs (str k) 1)
                                                                              "="
                                                                              (if (keyword? v)
                                                                                (subs (str v) 1)
                                                                                (str v)))))]
                                                 (element :meta {:name nm :content content})))
                                               (apply-meta-rule
                                                (if (= this-tag :platform)
                                                  ""
                                                  (str this-tag "-"))
                                                  k (get val k) rule)))

                             (set? rule)
                             (do ;;(println "meta set rule: " k rule)
                                 (let [val-param (get val k)]
                                   ;; (println "val: " val-param)
                                   (if (contains? rule val-param)
                                     (let [nm (str tag this-tag "-" k-tag)
                                           content (subs (str val-param) 1)]
                                       (element :meta {:name nm :content content}))
                                     (throw (Exception. (str "META: unrecognized enum option: "
                                                             key " {" k " " v"}"))))))

                             (vector? rule)
                             (do ;;(log/trace "meta vector rule: " k ", " val ": " rule)
                               (let [v (val k)]
                                 ;;(log/trace "found val: " v)
                                 (if (= v (first rule))
                                   (let [nm (if (= "platform-" tag)
                                              (str this-tag "-" k-tag)
                                              (str tag this-tag "-" k-tag))
                                         content (second rule)]
                                     ;;(log/trace nm  "=\"" content "\"")
                                     (element :meta {:name nm :content content}))
                                   (throw (Exception. (str "META: unrecognized option: " key " {" k " " v"}"))))))
                             :else (throw (Exception.
                                           (str "META: unrecognized option type: "
                                                key " {" k " " v"}" (type rule)))))))))]
      (doall result)
    result)))
