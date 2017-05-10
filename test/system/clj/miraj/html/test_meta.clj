(ns miraj.html.test-meta
  (:require [miraj.co-dom :as codom]
            [miraj.html :as h]
            [miraj.html.x.apple :as apple]
            [miraj.html.x.ms :as ms]
            ;; [miraj.polymer :as p]
            :reload))

(def apple-meta
  #::apple{:mobile #::apple{:status-bar-style :black
                            :title "Hello"}
           :touch #::h{:icons [#::h{:href "icon-iphone.png"}
                               #::h{:sizes [[152 152]] :href "icon-ipad.png"}
                               #::h{:sizes [[180 180]] :href "iphone-retina.png"}
                               #::h{:sizes [[167 167]] :href "ipad-retina.png"}]
                       ::apple/startup-image "/launch.png"}})

(def ms-meta
       #::ms{
             ;; <meta name="msapplication-allowDomainApiCalls" content="true">
             ;; <meta name="msapplication-allowDomainMetaTags" content="true">
             :allow-domain [::ms/api-calls ::ms/meta-tags]

             ;; <meta name="msapplication-badge"
             ;;       content="frequency=30; polling-uri=http://example.com/id45453245/polling.xml">
             :badge #::ms{:frequency 30  ;; default: 1440 minutes = 1 day
                          :polling-uri "http://example.com/id45453245/polling.xml"}

             ;; <meta name="msapplication-config" content=" http://contoso.com/browserconfig.xml"/>
             :config "http://contoso.com/browserconfig.xml"

             ;; <meta name="msapplication-navbutton-color" content="#FF3300">
             :navbutton #::ms{:color "#FFCCAA"}

             ;; <meta name="msapplication-notification"
             ;;    content="frequency=30;polling-uri=http://contoso.com/livetile;
             ;;    polling-uri2=http://contoso.com/livetile2;polling-uri3=http://contoso.com/livetile3;
             ;;    polling-uri4=http://contoso.com/livetile4;polling-uri5=http://contoso.com/livetile5">
             :notification #::ms{:frequency 30
                                 :cycle 3
                                 :polling-uris ["http://contoso.com/livetile"
                                                "http://contoso.com/livetile2"
                                                "http://contoso.com/livetile3"
                                                "http://contoso.com/livetile4"
                                                "http://contoso.com/livetile5"]}

             ;; <meta name="msapplication-square150x150logo" content="images/logo.png">
             ;; <meta name="msapplication-square310x310logo" content="images/largelogo.png">
             ;; <meta name="msapplication-square70x70logo" content="images/tinylogo.png">
             ;; <meta name="msapplication-wide310x150logo" content="images/widelogo.png">
             :logos [{[150 150] "images/logo.png"}
                     {[310 310] "images/largelogo.png"}
                     {[70 70]   "images/largelogo.png"}
                     {[310 150] "images/widelogo.png"}]

             ;; <meta name="msapplication-starturl" content="./CalculatorHome.html" />
             :start-url "./CalculatorHome.html"

             ;; <meta name="msapplication-tap-highlight" content="no" />
             :tap-highlight false

             ;; <meta name="msapplication-task"
             ;;       content="name=Check Order Status;
             ;;       action-uri=./orderStatus.aspx?src=IE9;
             ;;       icon-uri=./favicon.ico" />
             :tasks [#::ms{:name "Msdn Flash Podcasts"
                           :action "./?topic=msdnFlash"
                           :icon   "Images/channel9_logo.ico"}
                     #::ms{:name "IE Podcasts"
                           :action "./?topic=connectedShow"
                           :icon   "Images/channel9_logo.ico"}
                     #::ms{:name "Other Podcasts"
                           :action "./?topic=other"
                           :icon   "Images/channel9_logo.ico"}
                     #::ms{:name "All Podcasts"
                           :action "./"
                           :icon   "Images/channel9_logo.ico"}
                     #::ms{:name "Check Order Status"
                           :action "./orderStatus.aspx?src=IE9"
                           :window-type :window
                           :separator 0
                           :icon   "./favicon.ico"}
                     #::ms{:name "footask"
                           :action "./action2"
                           :separator 2
                           :icon   "./favicon2.ico"}]

             ;; <meta name="msapplication-TileColor" content="#3372DF">
             ;; <meta name="msapplication-TileImage" content="tile-background.png">
             :tile #::ms{:color "#3372DF"
                         :image "tile-background.png"}

             ;; <meta name="msapplication-tooltip" content="Channel 9 Podcasts" />
             :tooltip "Channel 9 Podcasts"

             ;; <meta name="msapplication-window" content="width=1024;height=768" />
             :window #::ms{:w 1024 :h 768}
             })

(def platforms
  #::h{:platform (merge apple-meta ms-meta)})

       ;; #::mobile{:agent #::h{:format :html5
       ;;                       :url "http://example.org/"}
       ;;           :web-app-capable true}

(h/validate-html-meta platforms)

(codom/serialize (h/head (h/meta-map->elts platforms)))
(codom/pprint (h/head (h/meta-map->elts platforms)))

(def metas
  #::h{:title "Miraj HTML lib test page"
       :description "this page demonstrates usage of miraj.html and polymer"
       :pagination #::h{:prev "page2.html"
                        :next "page4.html"}})

(h/validate-html-meta metas)
(codom/pprint (h/head (h/meta-map->elts metas)))

(alias 'ms 'miraj.html.ms)

(def pragmas
  #::h{:pragma #::h{:x-ua-compatible "IE=edge,chrome=1"
                    ;; :content-language "en"   ;; obsolete
                    :content-security-policy "default-src https://cdn.example.net; child-src 'none'; object-src 'none'"
                    ;; :content-type "foo"   ;; obsolete
                    :default-style "main-style"
                    :refresh "20; URL=page4.html"
                    ;; :set-cookie "foo"  ;; obsolete
                    :pics-label "pics label here"
                    }})

(def links
  #::h{:alternate #::h{:type "application/pdf" :hreflang "fr" :href "manual-fr"}
       :author    #::h{:name "John Smith" ;; <meta name="author">John Smith</meta>
                       :href "smith.html" ;; <link rel="author" href="smith.html">
                       }
       :help {::h/href "help.html"}
       :icons [#::h{:href "favicon.png" :sizes [[16 16]] :type "image/png"}
               #::h{:href "windows.ico" :sizes [[32 32] [48 48]] :type "image/vnd.microsoft.icon"}
               #::h{:href "mac.icns"    :sizes [[128 128] [512 512] [8192 8192] [32768 32768]]}
               #::h{:href "iphone.png"  :sizes [[57 57]] :type "image/png"}
               #::h{:href "gnome.svg"   :sizes :any      :type "image/svg+xml"}]
       :license "epl1.html"
       :prefetch "myimage.html"
       :search "mysearch.html"
       :stylesheets #::h{:persistent [#::h{:href "reset.css"}
                                      #::h{:href "style.css"}]
                         :preferred #::h{:label "Default"
                                         :href "default.css"
                                         :referrerpolicy "origin"
                                         :cross-origin :anonymous}
                         :alternates [#::h{:title "Fancy" :href "fancy.css" :cross-origin :anonymous}
                                      #::h{:title "Basic" :href "basic.css" :media "print"}
                                      ]}
       ;; free-form:  :rel required
       :links [;; <link rel='archives' title='May 2003' href='http://blog.unto.net/2003/05/'>
               #::h{:rel "archives" :title "May 2003" :href "http://blog.unto.net/2003/05/"}
               ;; <link rel='bookmark' title='Styleguide' href='http://paulrobertlloyd.com/about/styleguide/'>
               #::h{:rel "bookmark" :title "Styleguide" :href "http://paulrobertlloyd.com/about/styleguide/"}]
       })

(h/validate-html-meta links)

(codom/pprint (h/head
 (h/meta-map->elts links)))

(h/meta-map->elts html-meta)

(def fooC :x)

(def meta-b
  #::h{:title"Miraj demo: hello-world"
       :description "This page demonstrates of a simple miraj webpage."
       :charset "utf-8"
       :viewport #::h{:width :device-width
                      :scale #::h{:min 0.5
                                  :max 2
                                  :initial 1}
                      :user-scalable true}
       :pragma #::h{:x-ua-compatible "IE=edge,chrome=1"
                    ;; :content-language "en"   ;; obsolete
                    ;; :content-security-policy "default-src https://cdn.example.net; child-src 'none'; object-src 'none'"
                    ;; :content-type "foo"   ;; obsolete
                    :default-style "main-style"
                    ;; :refresh "20; URL=page4.html"
                    ;; :set-cookie "foo"  ;; obsolete
                    :pics-label "pics label"
                    }

       :icons [#::h{:href "favicon.png" :sizes #{[16 16]} :type "image/png"}
               #::h{:href "mac.icns" :sizes #{[128 128] [512 512] [8192 8192] [32768 32768]}}]

       ;; invalid keys (ignored):
       :fooB {:bar 9}
       ;; fooC 99
       \a "abc"
       0 true
       :bar "foo test"})
