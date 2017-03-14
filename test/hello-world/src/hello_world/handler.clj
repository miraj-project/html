(ns hello-world.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :as h]
            [miraj.html :as m]
            [miraj.markup :as x]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/h" []
       (h/html [:div "hi" "world"]))

  (GET "/m" []
       (x/serialize :xml (m/html (m/div "HI world"))))

  (GET "/s" []
       (pr-str (m/div "HI world")))

  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
