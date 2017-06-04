(ns miraj.html.x.apple
  "Namespace for specking Apple HTML meta tags"
  (:require [clojure.spec.alpha :as s]
            [miraj.html :as h]
            [miraj.co-dom :as codom :refer [element co-dom-node?]]
            [clojure.tools.logging :as log :only [trace debug info warn error]]))


;; APPLE https://developer.apple.com/library/content/documentation/AppleApplications/Reference/SafariHTMLRef/Articles/MetaTags.html
;; https://developer.apple.com/library/content/documentation/AppleApplications/Reference/SafariWebContent/ConfiguringWebApplications/ConfiguringWebApplications.html

;; <meta name="apple-mobile-web-app-capable" content="yes" />
;; <meta name="apple-mobile-web-app-title" content="Myname">
;; <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
;; <link rel="apple-touch-icon" href="touch-icon-iphone.png">
;; <link rel="apple-touch-icon" sizes="152x152" href="touch-icon-ipad.png">
;; <link rel="apple-touch-icon" sizes="180x180" href="touch-icon-iphone-retina.png">
;; <link rel="apple-touch-icon" sizes="167x167" href="touch-icon-ipad-retina.png">
;; <link rel="apple-touch-startup-image" href="/launch.png">
;; <meta name="format-detection" content="telephone=no">

(s/def ::startup-image string?)
(h/register-xform ::startup-image (fn [k v] {k v}))

;; :touch #::h{:icons [#::h{:sizes [[152 152]] :href "icon-ipad.png"}
;; <link rel="apple-touch-icon" sizes="152x152" href="icon-ipad.png">
(s/def ::touch (s/and (s/keys :opt [::icons ::startup-image])
                      (fn [x]
                        (every? #(contains? #{::h/icons ::startup-image} %) (keys x)))))

(h/register-xform ::touch (fn [k v]
                          ;; (log/debug (format "TOUCH %s" (seq v)))
                          (let [maps (filter codom/attr-map? v)
                                ;; _ (log/debug (format "MAPS %s" (seq maps)))
                                elts (filter #(not (codom/attr-map? %)) v)
                                ;; _ (log/debug (format "TOUCH ELTS %s" elts))
                                icons (into [] (for [elt (first elts)]
                                                 (do ;; (log/debug "T ELT:" elt (co-dom-node? elt))
                                                     (if (and (co-dom-node? elt)
                                                              (= "icon" (-> elt :attrs :rel)))
                                                       (merge (:attrs elt)
                                                              {:rel "touch-icon"})
                                                       elt))))
                                ;; _ (log/debug (format "ICONS %s" icons))
                                res (apply merge {::icons icons} maps)
                                ;; _ (log/debug (format "TOUCH result %s" res))

                                touch-elts (for [[k v] res]
                                             (do ;; (log/debug (format "Touch ELT %s %s" k v))
                                                 (if (= ::icons k)
                                                   (for [icon v]
                                                     (element :link (merge
                                                                     (dissoc icon :rel)
                                                                     {:rel "apple-touch-icon"})))
                                                   (if (= ::startup-image k)
                                                     (element :link {:rel "apple-touch-startup-image"
                                                                     :href v})))))
                                ]
                            ;; (log/debug (format "TOUCH ELTS %s" touch-elts))
                             touch-elts)))

(s/def ::status-bar-style (fn [x]
                            (contains? #{:default :black :black-translucent} x)))

(h/register-xform ::status-bar-style (fn [k v] {k (clojure.core/name v)}))

(s/def ::title string?)
;; (h/register-xform ::title (fn [k v] (element :title v)))

(s/def ::mobile (s/and (s/keys :opt [::status-bar-style ::title])
                       (fn [x] (every? #(contains? #{::status-bar-style ::title} %) (keys x)))))

(h/register-xform ::mobile (fn [k v]
                           ;; (log/debug (format "MOBILE %s" (seq v)))
                           (let [mob (for [elt v]
                                       (do ;; (log/debug "MOB ELT:" elt (co-dom-node? elt))
                                           (if (co-dom-node? elt)
                                             {::title (first (:content elt))}
                                             elt)))
                                 res (into {::web-app-capable "yes"} mob)
                                 ;; _ (log/debug (format "MOBILE RES %s" res))
                                 result (for [[k v] res]
                                          (do ;; (log/debug (format "mELT %s %s" k v))
                                              (if (= ::title k)
                                                (element :meta {:name "apple-mobile-web-app-title"
                                                                :content v})
                                                (if (= ::status-bar-style k)
                                                  (element :meta {:name "apple-mobile-web-app-status-bar-style"
                                                                  :content v})
                                                  (if (= ::web-app-capable k)
                                                    (element :meta {:name "apple-mobile-web-app-capable"
                                                                    :content "yes"}))))))]
                             ;; (log/debug (format "MOBILE Result %s" (seq result)))
                             result)))

(s/def ::apple (s/and (s/keys :opt [::format-detection ::itunes-app ::mobile ::touch])
                      (fn [x]
                        (every? #(contains? #{::format-detection ::itunes-app ::mobile ::touch} %)
                                       (keys x)))))

(h/register-xform ::apple (fn [k v]
                          ;; (log/debug (format "APPLE %s" (into {} v)))
                          (let [m (into {} v)
                                mobile (::mobile m)
                                ;; _ (log/debug (format "MOBILE %s" mobile))
                                mob-elts (for [[k v] mobile]
                                         (do ;; (log/debug (format "ELT %s %s" k v))
                                             (if (= ::title k)
                                               (element :meta {:name "apple-mobile-web-app-title"
                                                               :content v})
                                               (if (= ::status-bar-style k)
                                                 (element :meta {:name "apple-mobile-web-app-status-bar-style"
                                                               :content v})
                                                 (if (= ::web-app-capable k)
                                                   (element :meta {:name "apple-mobile-web-app-capable"
                                                                   :content "yes"}))))))
                                touch  (::touch m)
                                ;; _ (log/debug (format "TOUCH %s" touch))
                                touch-elts (for [[k v] touch]
                                             (do ;; (log/debug (format "Touch ELT %s %s" k v))
                                                 (if (= ::icons k)
                                                   (for [icon v]
                                                     (element :link (merge
                                                                     (dissoc icon :rel)
                                                                     {:rel "apple-touch-icon"})))
                                                   (if (= ::startup-image k)
                                                     (element :link {:rel "apple-touch-startup-image"
                                                                     :href v})))))
                                ;; _ (log/debug (format "TOUCH ELTS %s" touch-elts))
                                result (concat mob-elts touch-elts)]
                            ;; (log/debug (format "RES %s" (seq result)))
                            result)))
