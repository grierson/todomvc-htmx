(ns service.system
  (:require
   [donut.system :as ds]
   [service.routes :as routes]
   [org.httpkit.server :as hk-server])
  (:gen-class))

(def system
  {::ds/defs
   {:http {:server #::ds{:config {:port 3000}
                         :start (fn [{:keys [::ds/config]}]
                                  (hk-server/run-server
                                   #'routes/app
                                   {:port (:port config)
                                    :join? false
                                    :async? true
                                    :legacy-return-value? false}))
                         :stop  (fn [{:keys [::ds/instance]}]
                                  (hk-server/server-stop! instance))}}}})

(defn start [opts]
  (ds/start
   system
   {[:http :server ::ds/config] {:port (:port opts 3000)}}))

(defn stop [sut] (ds/stop sut))
