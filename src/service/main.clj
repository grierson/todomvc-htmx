(ns service.main
  (:require
   [donut.system :as ds]
   [service.service :as service]
   [org.httpkit.server :as hk-server])
  (:gen-class))

(defn start [handler]
  (hk-server/run-server
   handler
   {:port 3000
    :join? false
    :async? true
    :legacy-return-value? false}))

(defn stop [server]
  (hk-server/server-stop! server))

(def system
  {::ds/defs
   {:http {:server #::ds{:start (fn [_]
                                  (start #'service/app))
                         :stop  (fn [{:keys [::ds/instance]}]
                                  (stop instance))}}}})

(comment
  (def a (ds/signal system ::ds/start))
  (ds/signal a ::ds/stop))
