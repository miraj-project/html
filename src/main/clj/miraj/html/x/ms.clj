(ns miraj.html.x.ms
  "Namespace for specking Microsoft HTML meta tags"
  (:require [clojure.spec :as s]
            [clojure.string :as str]
            [miraj.html :as h]
            [miraj.co-dom :as codom :refer [element co-dom-node?]]
            [clojure.tools.logging :as log :only [trace debug info warn error]]))

;;;; WINDOWS
;; https://msdn.microsoft.com/en-us/library/gg491732(v=VS.85).aspx
;; pinned site meta ref: https://msdn.microsoft.com/en-us/library/dn255024(v=vs.85).aspx

;; <meta name="msapplication-allowDomainApiCalls" content="true">
;; <meta name="msapplication-allowDomainMetaTags" content="true">
;;  ::ms = miraj.html.x.ms
;; ::h{:ms #::ms{:allow-domain [::ms/api ::ms/meta]}}
(defn- allow-domain-kw? [x] (contains? #{::api-calls ::meta-tags} x))

(s/def ::allow-domain (s/coll-of allow-domain-kw?))
(h/register-xform ::allow-domain (fn [k v]
                                   ;; (log/debug (format "ALLOW-DOMAIN %s" (seq v)))
                                   (let [elts (for [kw v]
                                                (do (log/debug (format "ELT %s" kw))
                                                    (if (= ::api-calls kw)
                                                      (element :meta
                                                               {:name "msapplication-allowDomainApiCalls"
                                                             :content "true"})
                                                      (if (= ::meta-tags kw)
                                                        (element :meta
                                                                 {:name "msapplication-allowDomainMetaTags"
                                                                  :content "true"})))))]
                                     ;; (log/debug (format "ALLOW-DOM ELTS %s" (seq elts)))
                                     elts)))

;; allowed freqs: 30, 60, 360 (6 hours), 720 (12 hours), and 1440 (1 day).
(s/def ::frequency (fn [x] (contains? #{30 60 360 720 1440} x)))
(s/def ::polling-uri string?)

;; :badge #::ms{:frequency 30 :polling-uri "http://example.com/id45453245/polling.xml"}
(s/def ::badge (s/and (s/keys :req [::polling-uri] :opt [::frequency])
                      (fn [x] (every? #(contains? #{::frequency ::polling-uri} %) (keys x)))))

;; <meta name="msapplication-badge"
;;       content="frequency=30; polling-uri=http://example.com/id45453245/polling.xml">
(h/register-xform ::badge (fn [k v]
                            (let [m (into {} v)]
                            (log/debug (format "BADGE %s" (seq m)))
                            (let [freq (or (::frequency m) 1440)
                                  uri (::polling-uri m)]
                            (element :meta {:name "msapplication-badge"
                                            :content (str "frequency=" freq "; "
                                                          "polling-uri=" uri)})))))

;; <meta name="msapplication-config" content=" http://contoso.com/browserconfig.xml"/>
(s/def ::config string?)
(h/register-xform ::config (fn [k v] (element :meta {:name "msapplication-config" :content v})))

;; <meta name="msapplication-navbutton-color" content="#FF3300">
(s/def ::color string?)
(s/def ::navbutton (s/and (s/keys :req [::color])
                          (fn [x] (every? #(contains? #{::color} %) (keys x)))))
(h/register-xform ::navbutton (fn [k v] (element :meta {:name "msapplication-navbutton-color"
                                                        :content (::color (first v))})))

(s/def ::cycle (fn [x] (and (>= x 0) (< x 8))))
(s/def ::polling-uris (s/coll-of string? :kind vector? :max-count 5))
(s/def ::notification (s/and (s/keys :req [::polling-uris] :opt [::cycle ::frequency])
                             (fn [x] (every? #(contains? #{::cycle ::frequency ::polling-uris} %)
                                             (keys x)))))
;; <meta name="msapplication-notification"
;;       content="frequency=30;polling-uri=http://contoso.com/livetile;
;;                polling-uri2=http://contoso.com/livetile2;polling-uri3=http://contoso.com/livetile3;
;;                polling-uri4=http://contoso.com/livetile4;polling-uri5=http://contoso.com/livetile5">
(h/register-xform ::notification
                  (fn [k v]
                    (let [m (into {} v)
                          freq (or (::frequency m) 1440)
                          uris (::polling-uris m)
                          cycle (or (::cycle m) nil)]
                      (log/debug (format "NOTIFICATION %s" m))
                      (element :meta {:name "msapplication-notification"
                                      :content (str "frequency=" freq ";"
                                                    (if cycle (str "cycle=" cycle ";"))
                                                    (str/join ";"
                                                              (for [[idx uri] (map-indexed vector uris)]
                                                                (str
                                                                 "polling-uri"
                                                                 (if (= 0 idx) "" (inc idx))
                                                                 "=" uri))))}))))

;; :logos [{:square [150 150] :href "images/logo/png"}
;;         {:wide [310 150] :href "images/widelogo.png"}]
;; <meta name="msapplication-square150x150logo" content="images/logo.png">
;; <meta name="msapplication-square310x310logo" content="images/largelogo.png">
;; <meta name="msapplication-square70x70logo" content="images/tinylogo.png">
;; <meta name="msapplication-wide310x150logo" content="images/widelogo.png">
(defn- logo? [x] (and (map? x)
                      (let [k (keys x)]
                        (and (= (count k) 1)
                             (string? (get x (first k)))
                             (or (= (first k) [70 70])
                                 (= (first k) [150 150])
                                 (= (first k) [310 310])
                                 (= (first k) [310 150]))))))

(s/def ::logos (s/coll-of logo? :kind vector?))
(h/register-xform ::logos
                  (fn [k logos]
                    (for [logo logos]
                      (let [k (first (keys (first logo)))
                            name (case k
                                   [70 70] "msapplication-square70x70logo"
                                   [150 150] "msapplication-square150x150logo"
                                   [310 310] "msapplication-square310x310logo"
                                   [310 150] "msapplication-wide310x150logo")]
                        (element :meta {:name name
                                        :content (get (first logo) k)})))))

;; <meta name="msapplication-starturl" content="./CalculatorHome.html" />
(s/def ::start-url string?)
(h/register-xform ::start-url (fn [k v] (element :meta {:name "msapplication-starturl"
                                                        :content v})))

;; https://msdn.microsoft.com/en-us/library/bg182645(v=vs.85).aspx
;; <meta name="msapplication-tap-highlight" content="no" />
(s/def ::tap-highlight boolean?)
(h/register-xform ::tap-highlight (fn [k v] (element :meta {:name "msapplication-tap-highlight"
                                                            :content (if v "yes" "no")})))

;; https://msdn.microsoft.com/en-us/library/gg491725(v=vs.85).aspx
;; <meta name="msapplication-task"
;;       content="name=Check Order Status;
;;       action-uri=./orderStatus.aspx?src=IE9;
;;       icon-uri=./favicon.ico" />
;; <meta name="msapplication-task-separator" content="1">
;; <meta name="msapplication-task-separator" content="2">
(s/def ::name string?)
(s/def ::action string?)
(s/def ::icon string?)
(s/def ::separator number?)
(s/def ::window-type (fn [x] (contains? #{:tab :self :window} x)))
(s/def ::task (s/and (s/keys :req [::name ::action ::icon] :opt [::separator ::window-type])
                     (fn [x] (every? #(contains? #{::name ::action ::icon ::separator ::window-type} %)
                                     (keys x)))))
(s/def ::tasks (s/coll-of ::task :kind vector?))
(h/register-xform ::tasks (fn [k v]
                            (log/debug (format "TASKs %s" (seq v)))
                            (for [task v]
                              ;;(do (log/debug (format "TASK %s" (seq task)))
                              (let [task (into {} task)
                                    name   (::name task)
                                    action (::action task)
                                    icon   (::icon task)
                                    w      (::window-type task)
                                    sep (if (::separator task)
                                          (element :meta {:name "msapplication-task-separator"
                                                          :content (str (::separator task))})
                                          nil)]
                                (list
                                 (element :meta {:name "msapplication-task"
                                                 :content (str "name=" name ";"
                                                               "action-uri=" action ";"
                                                               "icon-uri=" icon ";"
                                                               (if w (str "window-type="
                                                                          (case w
                                                                            :tab "tab;"
                                                                            :self "self;"
                                                                            :window "window;"))
                                                                   ""))})
                                 sep)))))

;; <meta name="msapplication-TileColor" content="#FF0000">
;; <meta name="msapplication-TileImage" content="tile-background.png">

(s/def ::image string?)
(s/def ::tile (s/and (s/keys :opt [::color ::image])
                     (fn [x] (every? #(contains? #{::color ::image} %) (keys x)))))
(h/register-xform ::tile (fn [k v]
                           (let [m (into {} v)
                                 _ (log/debug (format "TILE %s" m))
                                 elts (if (::color m)
                                        (element :meta {:name "msapplication-TileColor"
                                                        :content (::color m)}))
                                 elts (if (::image m)
                                        (list elts
                                              (element :meta {:name "msapplication-TileImage"
                                                              :content (::image m)})))]
                             elts)))


;; <meta name="msapplication-tooltip" content="Channel 9 Podcasts" />
(s/def ::tooltip string?)
(h/register-xform ::tooltip (fn [k v] (element :meta {:name "msapplication-tooltip"
                                                      :content v})))

;; <meta name="msapplication-window" content="width=1024;height=768" />
(s/def ::w  number?)
(s/def ::h number?)
(s/def ::window (s/and (s/keys :req [::w ::h])
                       (fn [x] (every? #(contains? #{::w ::h} %) (keys x)))))
(h/register-xform ::window (fn [k v]
                             (let [m (into {} v)]
                             (log/debug (format "WINDOW %s" m))
                             (element :meta {:name "msapplication-window"
                                             :content (str "width=" (::w m) ";"
                                                           "height=" (::h m))}))))
