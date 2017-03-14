(in-ns 'miraj.html)

(require '[clojure.spec :as spec])

(spec/def ::description string?)

(spec/def ::title string?)
(spec/def ::base string?)

;; (def mobile-meta-tags
;;   {:mobile {:agent ^:compound {:format #{:wml :xhtml :html5}
;;                                :url :_}
;;             :web-app {:capability :fullscreen}}})


(spec/def ::uri uri?)

(spec/def ::icon (spec/keys :req [::uri]))

(spec/def ::web-app-capable (fn [x] (instance? Boolean x)))

(spec/def ::status-bar-style (fn [x] (contains? #{:default :black :black-translucent} x)))

(spec/def ::telephone-number-detection (fn [x] (= :disable)))

(spec/def ::app (spec/keys :opt [::id ::argument]))

(spec/def ::itunes-app (spec/keys :opt [::app ::affiliate-data]))

(spec/def ::mobile-web-app (spec/keys :opt [::status-bar-style ::title]))

(spec/def ::touch (spec/keys :req [::foo] :opt [::icon ::startup-image]))

(spec/def ::apple (spec/keys :opt [::format-detection
                                   ::itunes-app
                                   ::mobile-web-app
                                   ::touch]))

(spec/def ::platform (spec/keys :opt [::apple ::ms]))

(spec/def ::meta (spec/keys :opt [::title ::description ::platform]))

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

;; https://www.w3.org/TR/html5/document-metadata.html#standard-metadata-names
#_(def html5-meta-attribs-standard
  #:html{:charset :encoding-decl   ;; :content is implicit as value of map entry
         ;; standard vals for name attrib
         :application-name :string
         :author :string
         :description :string
         :generator :string
         :keywords :tokens})


#_(def html5-pseudo-meta-attribs
  #:html{:title 'FIXME
         :base  'FIXME})

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

#_(def html5-link-meta
  ;; link elements treated as meta-data
 {:icon ^:compound {:uri :uri :sizes :sizes}
  :manifest :uri})

#_(def html5-miraj-meta-attribs
  {:platform (merge apple-meta-tags ms-meta-tags mobile-meta-tags)})

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
