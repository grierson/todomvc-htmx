(ns service.core
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [service.system :as system])
  (:gen-class))

(defn -main
  [overrides]
  (let [config (aero/read-config (io/resource "config.edn"))
        {:keys [port]} (merge config overrides)]
    (system/start {:port port
                   :db (sorted-map)})))
