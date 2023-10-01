(ns service.application
  (:require [service.database :as database]))

; (def todos
;   (atom
;    (sorted-map 1 {:id 1 :name "Learn vi" :done false}
;                2 {:id 2 :name "Build todo app with HTMX and Clojure" :done false})))

(def todos-id (atom 0))

(defn add-todo! [db name]
  (let [id (swap! todos-id inc)]
    (database/add-todo! db id name)))

(defn update-todo! [db id name]
  (database/update-todo! db id name))

(defn toggle-todo! [db id]
  (database/toggle-todo! db id))

(defn remove-todo! [db id]
  (database/remove-todo! db id))

(defn remove-all-completed-todo [db]
  (database/remove-all-complete db))
