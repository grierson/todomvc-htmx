(ns service.system
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [donut.system :as ds]
   [service.routes :as routes]
   [org.httpkit.server :as hk-server]))

(def system
  {::ds/defs
   {:env {}
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

(defn init [{:keys [port db]}]
  (ds/start
   system
   {[:env :http :port] port
    [:infrastucture :db ::ds/start] (fn [_] (atom db))}))

(defn start [overrides]
  (let [config (aero/read-config (io/resource "config.edn"))
        {:keys [port]} (merge config overrides)]
    (init {:port port
           :db (sorted-map)})))

(defn stop [sut] (ds/stop sut))
