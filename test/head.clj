(ns test.head
  (:require [miraj.html :as h]
            [miraj.markup :refer :all]))

;; (defn platform
;;   [{:keys [apple ms mobile]}]
;;   (println "apple: " apple)
;;   (println "ms: " ms)
;;   (println "mobile: " mobile)
;;   (merge (h/apply-meta-rule "" :apple apple (:apple h/apple-meta-tags))
;;          (h/apply-meta-rule "" :msapplication ms (:msapplication h/ms-meta-tags))
;;          (h/apply-meta-rule "" :mobile mobile (:mobile h/mobile-meta-tags))))

(pprint (h/head
         (h/platform {:apple {:mobile-web-app {:capable true
                                               :status-bar-style :black
                                               :title "Hello"}}
                      :ms {:navbutton-color "#FFCCAA"
                           :tile-color "#3372DF"
                           :tile-image "images/ms-touch-icon-144x144-precomposed.png"}
                      :mobile {:agent {:format :html5
                                       :url "http://3g.sina.com.cn/"}
                               :web-app-capable true}})))

;; or:

;; (require '(miraj.htm [platform :as platform]))
;; (h/head
;;  (platform/apple {:mobile-web-app {:capable true
;;                                    :status-bar-style :black
;;                                    :title "Hello"}})
;;  (platform/ms {:navbutton-color "#FFCCAA"
;;                :tile-color "#3372DF"
;;                :tile-image "images/ms-touch-icon-144x144-precomposed.png"})
;;  (platform/mobile {:agent {:format :html5
;;                            :url "http://3g.sina.com.cn/"}
;;                    :web-app-capable true}))
