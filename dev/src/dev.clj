(ns dev
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [donut.system :as ds]
   [donut.system.repl :as dsr]
   [service.system :as system]))

(defmethod ds/named-system :donut.system/repl
  [_]
  system/system)

(comment
  (try
    (let [{:keys [port]} (aero/read-config (io/resource "config.edn"))]
      (dsr/start
       ::ds/repl
       {[:env :http :port] (or port 3000)}))
    (catch Exception e
      (print e)))
  (dsr/restart)
  (dsr/stop))
