(ns service.system
  (:require
   [donut.system :as ds]
   [service.routes :as routes]
   [org.httpkit.server :as hk-server])
  (:gen-class))

(def system
  {::ds/defs
   {:env #::ds{:config {:port 3000}}
    :infrastucture {:db #::ds{:start (fn [_] (atom (sorted-map)))}}
    :http {:server #::ds{:config {:port (ds/ref [:env ::ds/config :port])
                                  :handler (ds/ref [:http :handler])}
                         :start (fn [{:keys [::ds/config]}]
                                  (hk-server/run-server
                                   (:handler config)
                                   {:port (:port config)
                                    :join? false
                                    :async? true
                                    :legacy-return-value? false}))
                         :stop  (fn [{:keys [::ds/instance]}]
                                  (hk-server/server-stop! instance))}
           :handler #::ds{:config {:db (ds/ref [:infrastucture :db])}
                          :start (fn [{:keys [::ds/config]}]
                                   (routes/app config))}}}})

(defn start [opts]
  (ds/start
   system
   {[:env ::ds/config] {:port (:port opts 3000)}}))

(defn stop [sut] (ds/stop sut))
