(ns service.system
  (:require
   [donut.system :as ds]
   [service.routes :as routes]
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
                                  (start #'routes/app))
                         :stop  (fn [{:keys [::ds/instance]}]
                                  (stop instance))}}}})
