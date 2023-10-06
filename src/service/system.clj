(ns service.system
  (:require
   [donut.system :as ds]
   [service.routes :as routes]
   [org.httpkit.server :as hk-server]))

(def system
  {::ds/defs
   {:env {:http {:port 3000}}
    :infrastucture {:db #::ds{:start (fn [_] (atom (sorted-map)))}}
    :http {:server #::ds{:config {:port (ds/ref [:env :http :port])
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

(defn start [{:keys [port db]}]
  (ds/start
   system
   {[:env :http :port] port
    [:infrastucture :db ::ds/start] (fn [_] (atom db))}))

(defn stop [sut] (ds/stop sut))
