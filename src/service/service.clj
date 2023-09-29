(ns service.service
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli :as mcoercion]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [muuntaja.core :as m]
            [hiccup2.core :as h]))

(defn- index-page
  [_]
  {:status 200
   :body (->> [:html
               [:head
                [:title "Hello world!"]
                [:script {:src "https://unpkg.com/htmx.org@1.9.6"
                          :integrity "sha384-FhXw7b6AlE/jyjlZH5iHa/tTe9EpJ1Y55RjcgPbjeWMskSxZt1v9qkxLJWNJaGni"
                          :crossorigin "anonymous"}]]
               [:body
                [:p {:style {:background-color "red"}} "Hello from HTML"]
                [:div {:hx-put "/messages"} "Put To Messages"]]]
              (h/html {:mode :html})
              (str "\n"))})

(defn get-messages
  [_]
  {:status 200
   :body (str (h/html [:h1 "Messages here from server"]))})

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get {:handler index-page}}]
     ["/messages" {:put {:handler get-messages}}]
     ["/math" {:get {:parameters {:query {:x int?, :y int?}}
                     :responses  {200 {:body {:total int?}}}
                     :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                                   {:status 200
                                    :body   {:total (+ x y)}})}}]]
    {:data {:coercion   mcoercion/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         muuntaja/format-response-middleware
                         rrc/coerce-response-middleware]}})))
