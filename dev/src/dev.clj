(ns dev
  (:require [donut.system :as ds]
            [donut.system.repl :as dsr]
            [service.system :as system]))

(defmethod ds/named-system :donut.system/repl
  [_]
  system/system)

(comment
  (try (dsr/start)
       (catch Exception e
         (print e)))
  (dsr/restart)
  (dsr/stop))
