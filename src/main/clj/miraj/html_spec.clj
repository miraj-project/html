(in-ns 'miraj.html)

(require '[clojure.spec :as s]
         '[clojure.string :as str])

(def xform-registry (atom {}))

(defn register-xform [k f]
  ;; (log/debug (format "REGISTER XFORM %s" k))
  (swap! xform-registry assoc k f))

(defn ->meta
  [tag content]
  (miraj.html/meta {:name (clojure.core/name tag) :content content}))

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

(s/def ::author string?)
(register-xform ::author ->meta)

(s/def ::description string?)
(register-xform ::description ->meta)

(s/def ::generator string?)
(register-xform ::generator ->meta)

(s/def ::keywords ::tokens?)

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
                             (miraj.html/meta {:content (str/join ", " v)
                                               :name "viewport"})))


;; html5-pseudo-meta-attribs
(s/def ::title string?)
(register-xform ::title (fn [k v] (miraj.html/title v)))

(s/def ::base string?)

#_(def html5-link-meta
  ;; link elements treated as meta-data
 {:icon ^:compound {:uri :uri :sizes :sizes}
  :manifest :uri})

;; link rel="icon": https://www.w3.org/TR/html5/links.html#rel-icon
 ;; <link rel=icon href=favicon.png sizes="16x16" type="image/png">
 ;;  <link rel=icon href=windows.ico sizes="32x32 48x48" type="image/vnd.microsoft.icon">
 ;;  <link rel=icon href=mac.icns sizes="128x128 512x512 8192x8192 32768x32768">
 ;;  <link rel=icon href=iphone.png sizes="57x57" type="image/png">
 ;;  <link rel=icon href=gnome.svg sizes="any" type="image/svg+xml">

(s/def ::href string?)
(register-xform ::href (fn [k v] {:href (str v)}))

(s/def ::size (s/cat :w number? :h number?))
(register-xform ::size (fn [k v] ;;(log/debug (format "SIZE %s %s" k v))
                         ))

(s/def ::sizes (s/coll-of ::size :kind set?))
(register-xform ::sizes (fn [k v] ;; (log/debug (format "SIZES %s %s" k v))
                          {:sizes (str/join " " (for [[w h] v] (str w "x" h)))}))

(s/def ::type string?)
(register-xform ::type (fn [k v] {:type (str v)}))

(s/def ::icon (s/keys :req [::href]
                      :opt [::sizes ::type]))
(s/def ::icons (s/coll-of ::icon :kind vector?))
(register-xform ::icons (fn [k v] ;;(log/debug (format "ICONS %s %s" k v))
                          (for [icon v]
                            (do
                              ;; (log/debug (format "ICON %s" (into {} icon)))
                                        ;(let [attrs (for [attr v] (str
                              (miraj.html/link (merge {:rel "icon"}
                                                      (into {} icon)))))))



#_(def html5-miraj-meta-attribs
  {:platform (merge apple-meta-tags ms-meta-tags mobile-meta-tags)})


;; (def mobile-meta-tags
;;   {:mobile {:agent ^:compound {:format #{:wml :xhtml :html5}
;;                                :url :_}
;;             :web-app {:capability :fullscreen}}})


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
                           (log/debug (format "PRAGMA %s" (into {} v)))
                           (for [[k v] (into {} v)]
                             (do (log/debug (format "Pragma %s %s" k v))
                                 (miraj.html/meta {:http-equiv (clojure.core/name k)
                                                   :content v})))))

(s/def ::uri uri?)

(s/def ::web-app-capable (fn [x] (instance? Boolean x)))

(s/def ::status-bar-style (fn [x] (contains? #{:default :black :black-translucent} x)))

(s/def ::telephone-number-detection (fn [x] (= :disable)))

(s/def ::app (s/keys :opt [::id ::argument]))

(s/def ::itunes-app (s/keys :opt [::app ::affiliate-data]))

(s/def ::mobile-web-app (s/keys :opt [::status-bar-style ::title]))

(s/def ::touch (s/keys :req [::foo] :opt [::icon ::startup-image]))

(s/def ::apple (s/keys :opt [::format-detection
                                   ::itunes-app
                                   ::mobile-web-app
                                   ::touch]))

(s/def ::platform (s/keys :opt [::apple ::ms]))

(s/def ::meta (s/keys :opt [::title ::description ::platform]))
(register-xform ::meta (fn [k v] v))


;; https://developer.apple.com/library/content/documentation/AppleApplications/Reference/SafariWebContent/PromotingAppswithAppBanners/PromotingAppswithAppBanners.html

#_(def ms-meta-tags
  {:ms {:config :uri
        :navbutton-color :color
        :notification {:?frequency #{30 60 360 720 1440}
                       :?cycle #{0 1 2 3 4 5 6 7}
                       :uri :uri
                       :?uris [:uri] ;; up to 5 total
                       }
        :square-70x70-logo :uri
        :square-150x150-logo :uri
        :square-310x310-logo :uri
        :starturl :uri
        :task {:name :string :action-uri :uri :icon-uri :uri
               :window-type #{:tab :self :window}}
        :tile-color :color
        :tile-image :uri
        :tooltip :string
        :tap-highlight :no
        :wide-310x150-logo :uri
        :window {:width :pixels, :height :pixels}
        ;; other ms metas https://msdn.microsoft.com/library/dn255024(v=vs.85).aspx
        :ms-pinned ^:nonstandard {:allow-domain-api-calls :bool
                                  :allow-domain-meta-tags :bool
                                  :badge {:frequency #{30 60 360 720 1440}
                                          :polling-uri :uri}
                                  :start-url :uri
                                  :task-separator :_}}})

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
