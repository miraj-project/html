(ns scripts)

(alter-meta! *ns*
             (fn [m] (assoc m :co-ns true :resource-type :js)))

(def jquery {:uri "https://code.jquery.com/jquery-2.1.1.min.js"})

(def materialize {:uri "bower_components/Materialize/dist/js/materialize.min.js"})

(def polyfill {:uri "bower_components/webcomponentsjs/webcomponents.js"})
(def polyfill-min {:uri "bower_components/webcomponentsjs/webcomponents.min.js"})

(def polyfill-lite {:uri "bower_components/webcomponentsjs/webcomponents-lite.js"})
(def polyfill-lite-min {:uri "bower_components/webcomponentsjs/webcomponents-lite.min.js"})
