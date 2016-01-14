(ns styles.shared)

(alter-meta! *ns*
             (fn [m] (assoc m
                            :co-ns true
                            :resource-type :polymer-style-module)))

(def pfx "styles")

(def uri "shared.html")

;; dom-modules in styles/shared.html
(def base "base")

(def foo "foo-style")

(def bar "bar-style")
