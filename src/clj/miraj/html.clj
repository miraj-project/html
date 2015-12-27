;   Copyright (c) Gregg Reynolds. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns miraj.html
  (:refer-clojure :exclude [import map meta require time])
  (:require [miraj.markup :as m]
            [potemkin :refer [import-vars]]
            [clojure.string :as str]))

(import-vars [miraj.markup import normalize optimize require])

            ;; [clojure.tools.logging :as log :only [trace debug error info]]))

;; (println "loading miraj.html")

            ;;[clojure.data.xml :as xml]
            ;; [clojure.tools.logging :as log :only [trace debug error info]]
            ;; [clojure.string :as string]
            ;; [clojure.pprint :as pp]
  ;; (:import [java.io ByteArrayInputStream StringReader StringWriter]
  ;;          [java.util Properties]
  ;;          [javax.xml.parsers DocumentBuilder DocumentBuilderFactory]
  ;;          [javax.xml.transform.dom DOMSource]
  ;;          [javax.xml.transform OutputKeys TransformerFactory]
  ;;          [javax.xml.transform.stream StreamSource StreamResult]
  ;;          [org.jsoup Jsoup]
  ;;          [org.jsoup.parser Parser]))

;; http://www.w3.org/TR/html51/semantics.html#semantics

;; complete list of html5 tags:
;; http://www.w3.org/TR/html-markup/elements.html
;; as of 29 Nov 2015 (missing 'data'):

;; see also http://www.w3.org/html/wg/drafts/html/master/index.html#elements-3

;;FIXME: make a complete tag map, tags as keys, docstrings as vals, linked to w3c spec
;; then use element content categories with membership test
;; a – hyperlink CHANGED
;; abbr – abbreviation
;; address – contact information
;; area – image-map hyperlink
;; article – article NEW
;; aside – tangential content NEW
;; audio – audio stream NEW

;; b – offset text conventionally styled in bold CHANGED
;; base – base URL
;; bdi – BiDi isolate NEW
;; bdo – BiDi override
;; blockquote – block quotation
;; body – document body
;; br – line break
;; button – button

;; canvas – canvas for dynamic graphics NEW
;; caption – table title
;; cite – cited title of a work CHANGED
;; code – code fragment
;; col – table column
;; colgroup – table column group
;; command – command NEW

;; data - MISSING from list but in REC
;; datalist – predefined options for other controls NEW
;; dd – description or value
;; del – deleted text
;; details – control for additional on-demand information NEW
;; dfn – defining instance
;; dialog - dialog box or window; http://www.w3.org/html/wg/drafts/html/master/semantics.html#the-dialog-element
;; div – generic flow container
;; dl – description list
;; dt – term or name

;; em – emphatic stress
;; embed – integration point for plugins NEW

;; fieldset – set of related form controls
;; figcaption – figure caption NEW
;; figure – figure with optional caption NEW
;; footer – footer NEW
;; form – user-submittable form

;; h1 – heading
;; h2 – heading
;; h3 – heading
;; h4 – heading
;; h5 – heading
;; h6 – heading
;; head – document metadata container
;; header – header NEW
;; hr – thematic break CHANGED
;; html – root element

;; i – offset text conventionally styled in italic CHANGED
;; iframe – nested browsing context (inline frame)
;; img – image
;; input – input control CHANGED
;; ins – inserted text

;; kbd – user input
;; keygen – key-pair generator/input control NEW

;; label – caption for a form control
;; legend – title or explanatory caption
;; li – list item
;; link – inter-document relationship metadata

;; main - Container for the dominant contents of another element
;; map – image-map definition
;; mark – marked (highlighted) text NEW
;; math - MathML root
;; menu – menu of commands
;; menuitem - menu item
;; meta – text metadata
;; meter – scalar gauge NEW

;; nav – section with navigational links
;; noscript – fallback content for script

;; object – generic external content: image, nested browsing context, or plugin
;; ol – ordered list
;; optgroup – group of options
;; option – option in a list box or combo box control
;; output – result of a calculation in a form NEW

;; p – paragraph
;; param – initialization parameters for Object
;; pre – preformatted text
;; progress – progress indicator NEW

;; q – quoted text

;; rb - ruby base
;; rp – ruby parenthesis
;; rt – ruby annotation text
;; rtc – ruby annotation text container
;; ruby – ruby annotation

;; s – struck text CHANGED
;; samp – (sample) output
;; script – embedded script
;; section – section NEW
;; select – option-selection form control
;; small – side comment
;; source – media source for video or audio
;; span – generic span
;; strong – strong importance
;; style – style (presentation) information
;; sub – subscript
;; summary – summary, caption, or legend for a details control
;; sup – superscript
;; svg - SVG root
;; table – table
;; tbody – table row group
;; td – table cell
;; template - template
;; textarea – text input area
;; tfoot – table footer row group
;; th – table header cell
;; thead – table heading group
;; time – date and/or time NEW
;; title – document title
;; tr – table row
;; track – timed text track
;; u – unarticulated non-textual annotation
;; ul – unordered list
;; var – variable or placeholder text
;; video – video NEW
;; wbr – line-break opportunity NEW

;; http://www.w3.org/TR/2011/WD-html5-20110525/content-models.html#metadata-content-0
(def html5-metadata-tags
  ["base" "command" "link" "meta" "noscript" "script" "style" "title"
   "template"])

;; http://www.w3.org/TR/2011/WD-html5-20110525/content-models.html#sectioning-content-0
(def html5-sectioning-tags
  ["address" "article" "aside"
   "blockquote" "body" "details"
   "fieldset" "figure" "footer"
   "header" "nav" "section" "td"])

;; http://www.w3.org/TR/2011/WD-html5-20110525/content-models.html#heading-content-0
(def html5-heading-tags
  ["h1" "h2" "h3" "h4" "h5" "h6"])

;; according to https://developer.mozilla.org/en-US/docs/Web/HTML/Block-level_elements
(def html5-block-tags
  ["address"
   "canvas" "caption" "command"
   "datalist" "dd" "del" "details" "dfn" "div" "dl" "dt" "data"
   "figcaption" "form"
   "li"
   "main" "menu" "menuitem"
   "ol"
   "p" "pre"
   "title"
   "ul"])

;; phrasing content: http://www.w3.org/TR/2011/WD-html5-20110525/content-models.html#phrasing-content-0
(def html5-phrasing-tags
  ["a" "abbr" "area" "audio"
   "b" "bdi" "bdo" "br" "button"
   "cite" "code" "command"
   "datalist" "del" "dfn"
   "em" "embed"
   "i" "iframe" "img" "input" "ins"
   "kbd" "keygen"
   "label"
   "map" "mark" "math" "meter"
   "noscript"
   "object" "output"
   "progress"
   "q"
   "ruby"
   "s" "samp" "script" "select" "small" "span" "strong" "sub" "sup" "svg"
   "textarea" "time"
   "u"
   "var" "video"
   "wbr"])

(def html5-null-tags
  ["caption" "col" "colgroup"
   "head" "html"
   "legend"
   "optgroup" "option"
   "rp" "rt"
   "summary"
   "table" "tbody" "td" "tfoot" "th" "thead" "tr"])

(def html5-void-elt-tags
  ["area" "base" "br" "col"
   "embed" "hr" "img" "input"
   "keygen" "link" "meta" "param"
   "source" "track" "wbr"])

(def html5-rawtext-elts
  ["script" "style"])

;; http://www.w3.org/TR/html5/obsolete.html#non-conforming-features
(def html5-obsolete-tags
  {:applet "Use embed or object instead."
   :acronym "Use abbr instead."
   :basefont "Use appropriate elements or CSS instead."
   :bgsound "Use audio instead."
   :big "Use appropriate elements or CSS instead."
   :blink "Use appropriate elements or CSS instead."
   :center "Use appropriate elements or CSS instead."
   :dir "Use ul instead."
   :font "Use appropriate elements or CSS instead."
   :marquee "Use appropriate elements or CSS instead."
   :multicol "Use appropriate elements or CSS instead."
   :nobr "Use appropriate elements or CSS instead."
   :frame "Either use iframe and CSS instead, or use server-side includes to generate complete pages with the various invariant parts merged in."
   :frameset "Either use iframe and CSS instead, or use server-side includes to generate complete pages with the various invariant parts merged in."
   :noframes "Either use iframe and CSS instead, or use server-side includes to generate complete pages with the various invariant parts merged in."
   :hgroup "To mark up subheadings, consider putting the subheading into a p element after the h1- h6 element containing the main heading, or putting the subheading directly within the h1- h6 element containing the main heading, but separated from the main heading by punctuation and/or within, for example, a span class='subheading' element with differentiated styling. Headings and subheadings, alternative titles, or taglines can be grouped using the header or div elements."
   :isindex "Use an explicit form and text field combination instead."
   :listing "Use pre and code instead."
   :nextid "Use GUIDs instead."
   :noembed "Use object instead of embed when fallback is necessary."
   :plaintext "Use the 'text/plain' MIME type instead."
   :strike "Use del instead if the element is marking an edit, otherwise use s instead."
   :xmp "Use pre and code instead, and escape '<' and '&' characters as '&lt;' and '&amp;' respectively."
   :spacer "Use appropriate elements or CSS instead."
   :tt "Use appropriate elements or CSS instead."})

(def attrs-regex
  #" *[^>]* *>")

(def attrs-overlap-start-regex
  ;; e.g. for a, b, em, i, li etc
  ;; match <li>, <li >, <li/>, <li />
  #" */?>")

(def attrs-overlap-attrs-regex
  ;; e.g. for a, b, em, i, li etc
  ;; match <li>, <li >, <li/>, <li />, <li foo="bar">, <li foo="bar">
  #" +[^>]* */?>")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  ATTRIBS

(def attrs-regex
  #" *[^>]* *>")

(def attrs-overlap-start-regex
  ;; e.g. for a, b, em, i, li etc
  ;; match <li>, <li >, <li/>, <li />
  #" */?>")

(def attrs-overlap-attrs-regex
  ;; e.g. for a, b, em, i, li etc
  ;; match <li>, <li >, <li/>, <li />, <li foo="bar">, <li foo="bar">
  #" +[^>]* */?>")

(def encoding-decl-regex
  ;; https://encoding.spec.whatwg.org/#names-and-labels
  [#"(?i)unicode-1-1-utf-8"
   #"(?i)utf-8"
   #"(?i)utf8"])

;; global attrs: http://www.w3.org/html/wg/drafts/html/master/dom.html#global-attributes
;; meta standard names: :application-name, :author, :description, :generator, :keywords
;; :charset;  :itemprop?
;; extensions: https://wiki.whatwg.org/wiki/MetaExtensions
;; see https://gist.github.com/kevinSuttle/1997924
;; :viewport gets special treatment
;; e.g.  :dc {:created ".." :creator "foo" ...}


;; syntax:
;;    keyword values represent type constraints by name
;;    sets represent enum types
;;    vectors map clj value to html value, e.g. [true "yes"]
;;    quoted vals represent type, plus translate to <link> instead of <meta>

(def mobile-meta-tags
  {:mobile {:agent ^:compound {:format #{:wml :xhtml :html5}
                               :url :_}
            :web-app-capable [true "yes"]}})

(def apple-meta-tags
  ;; https://developer.apple.com/library/safari/documentation/AppleApplications/Reference/SafariHTMLRef/Articles/MetaTags.html
  {:apple {:itunes-app :_
           :mobile-web-app {:capable [true "yes"]
                            :status-bar-style #{:default :black :black-translucent}
                            :title :string}
           :touch {:icon 'icon  ;; meaning type is icon, generate <link"
                   :startup-image 'image
                   ;; apple-touch-fullscreen :_ ;; "not needed anymore"
           :format-detection {:disable "telephone=no"}}}})

(def ms-meta-tags
  {:msapplication {:config :uri
                   :navbutton-color :color
                   :notification [:uri]
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

(def twitter-meta-tags
  )

(def html5-meta-attribs-standard
  {:charset :encoding-decl   ;; :content is implicit as value of map entry
   ;; standard vals for name attrib
   :application-name :string
   :author :string
   :description :string
   :generator :string
   :keywords :tokens})

(def html5-meta-attribs-extended
   ;; extended name attribs https://wiki.whatwg.org/wiki/MetaExtensions
  {:viewport  {:width :pixels
                 :height :pixels
                 :initial-scale :number
                 :minimum-scale :number
                 :maximum-scale :number
                 :user-scalable #{:zoom :fixed}}
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

(def html5-meta-attribs
  (merge {} html5-meta-attribs-standard html5-meta-attribs-extended
         apple-meta-tags ms-meta-tags))

(def html5-tags
  (distinct
   (sort
    (concat html5-null-tags
            html5-metadata-tags html5-heading-tags html5-sectioning-tags html5-block-tags
            html5-phrasing-tags))))

(defn list-tags
  []
  (let [content-tags (remove (set html5-void-elt-tags) html5-tags)]
    ;; (println "ALL TAGS: " html5-tags)
    ;; (println "CONTENT TAGS: " content-tags)
    (doseq [tag html5-tags]
      (println tag))))

(def html5-pragma-directives
  "meta http-equiv pragma directives
  http://www.w3.org/html/wg/drafts/html/master/semantics.html#pragma-directives"
  ["http-equiv"
   ;; {fn-name [elt-tag <validation rule>]}
  {:content-language ["content-language" {:non-conforming "Authors are
  encouraged to use the lang attribute instead."}]
   :content-type ["content-type" :encoding-decl]
   :default-style ["default-style" :string]
   :refresh ["refresh" :refresh-syntax]
   :set-cookie ["set-cookie" {:non-conforming "Real HTTP headers should be used instead."}]
   ;; HTML 5.1
   :content-security-policy ["Content-Security-Policy" :string]
   ;; :x-ua-compatible
   }])

(m/make-meta-tag-fns html5-pragma-directives)

(m/make-tag-fns nil (remove (set html5-void-elt-tags) html5-tags) nil)

(m/make-void-elt-fns html5-void-elt-tags)

(defn apply-meta-rule
  [tag key val ruleset]
  ;; (log/trace (str "APPLY META RULE: " tag " | " key " | " val " | " ruleset))
  (let [this-tag (subs (str key) 1)]
    (for [[k v] val]
      (do ;;(log/trace "key: " k ", val: " val)
          (if-let [rule (get ruleset k)]
            (let [k-tag (subs (str k) 1)]
              ;;(log/trace "rule: " rule)
              (cond
                (keyword? rule)
                (do ;;(log/trace "meta keyword rule: " k ", " val ": " rule)
                    (let [val-param (get val k)
                          elt (condp = rule
                                :string (meta {:name (str tag this-tag "-" k-tag)
                                                 :content (str val-param)})
                                :number (meta {:name (str tag this-tag "-" k-tag)
                                                 :content (str val-param)})
                                :color (meta {:name (str tag this-tag "-" k-tag)
                                                :content (str val-param)})
                                :uri (meta {:name (str tag this-tag "-" k-tag)
                                              :content (str val-param)})
                                :_ (meta {:name (str tag this-tag "-" k-tag)
                                            :content (str val-param)})
                                ;; :tokens
                                )]
                      ;;(log/trace "elt: " elt)
                      elt))

                (map? rule) (do ;;(println "meta map key: " k)
                              ;;(println "meta map val: " (get val k))
                                (if (:compound (clojure.core/meta rule))
                                  (let [nm (str tag this-tag "-" k-tag)
                                        content (str/join
                                                       "; " (for [[k v] (get val k)]
                                                             (str (subs (str k) 1)
                                                                  "="
                                                                  (if (keyword? v)
                                                                    (subs (str v) 1)
                                                                    (str v)))))]
                                    (meta {:name nm :content content}))
                                (apply-meta-rule (str this-tag "-") k (get val k) rule)))

                (set? rule)
                (do ;;(log/trace "meta set rule: " k rule)
                    (let [val-param (get val k)]
                      ;;(log/trace "val: " val-param)
                      (if (contains? rule val-param)
                        (let [nm (str tag this-tag "-" k-tag)
                              content (subs (str val-param) 1)]
                          (meta {:name nm :content content}))
                        (throw (Exception. (str "META: unrecognized enum option: "
                                                key " {" k " " v"}"))))))

                (vector? rule)
                (do ;;(log/trace "meta vector rule: " k ", " val ": " rule)
                    (let [v (val k)]
                      ;;(log/trace "found val: " v)
                      (if (= v (first rule))
                        (let [nm (str tag this-tag "-" k-tag)
                              content (second rule)]
                          ;;(log/trace nm  "=\"" content "\"")
                          (meta {:name nm :content content}))
                        (throw (Exception. (str "META: unrecognized option: " key " {" k " " v"}"))))))
                :else (throw (Exception.
                              (str "META: unrecognized option type: "
                                   key " {" k " " v"}" (type rule)))))))))))

(defn get-metas
  [metas]
  ;; (log/trace "GET-METAS " metas)
  ;; (log/trace "HTML5-METAS " (keys h/html5-meta-attribs))
  (let [ms (for [[tag val] metas]
             (let [rule (get html5-meta-attribs tag)]
               ;;(log/trace "META: " tag val rule)
               (if (nil? rule) (throw (Exception. (str "unknown meta name: " (str tag)))))
               (case tag
                 ;; :description	(h/meta {:name "description" :content val})
                 ;; :title		(h/meta {:name "description" :content val})
                 ;; :application-name	(h/meta {:name "description" :content val})
                 :apple (let [apple (apply-meta-rule "" tag val rule)]
                          #_(log/trace "APPLE: " apple) apple)
                 :msapplication (let [ms (apply-meta-rule "msapplication" tag val rule)]
                                  #_(log/trace "MSAPP: " ms) ms)
                 :mobile (let [ms (apply-meta-rule "" tag val rule)]
                           #_(log/trace "MOBILE: " ms) ms)
                 ;; :theme-color	(h/meta {:name "description" :content val})
                 ;; :foo	(h/meta {:name "foo" :content val})
                 ;; :bar	(h/meta {:name "bar" :content val})
                 ;; :baz	(h/meta {:name "baz" :content val})
                 (meta {:name (subs (str tag) 1)
                          :content (str val)}))))]
    ;; (log/trace "Metas: " ms)
    ms))

(defn platform
  [{:keys [apple ms mobile]}]
  ;; (println "apple: " apple)
  ;; (println "ms: " ms)
  ;; (println "mobile: " mobile)
  (merge (apply-meta-rule "" :apple apple (:apple apple-meta-tags))
         (apply-meta-rule "" :msapplication ms (:msapplication ms-meta-tags))
         (apply-meta-rule "" :mobile mobile (:mobile mobile-meta-tags))))
