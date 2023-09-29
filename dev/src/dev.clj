(ns dev
  (:require [donut.system :as ds]
            [donut.system.repl :as dsr]
            [service.system :as system]))

(defmethod ds/named-system :donut.system/repl
  [_]
  system/system)

(comment
  (dsr/start)
  (dsr/restart)
  (dsr/stop))
