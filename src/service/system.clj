(ns service.system
  (:require
   [donut.system :as ds]
   [service.routes :as routes]
   [org.httpkit.server :as hk-server])
  (:gen-class))

(def system
  {::ds/defs
   {:http {:server #::ds{:start (fn [_]
                                  (hk-server/run-server
                                   #'routes/app
                                   {:port 3000
                                    :join? false
                                    :async? true
                                    :legacy-return-value? false}))
                         :stop  (fn [{:keys [::ds/instance]}]
                                  (hk-server/server-stop! instance))}}}})

(defn start [] (ds/start system))
(defn stop [sut] (ds/stop sut))
