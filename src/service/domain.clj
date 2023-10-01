(ns service.domain)

(def todos
  (atom
   (sorted-map 1 {:id 1 :name "Learn vi" :done false}
               2 {:id 2 :name "Build todo app with HTMX and Clojure" :done false})))

(def todos-id (atom (count @todos)))

(defn add-todo! [name]
  (let [id (swap! todos-id inc)]
    (swap! todos assoc id {:id id :name name :done false})))

(defn update-todo! [id name]
  (swap! todos assoc-in [(Integer. id) :name] name))

(defn toggle-todo! [id]
  (swap! todos update-in [id :done] not))

(defn remove-todo! [id]
  (swap! todos dissoc (Integer. id)))

(defn filtered-todo [filter-name todos]
  (case filter-name
    "active" (remove #(:done (val %)) todos)
    "completed" (filter #(:done (val %)) todos)
    "all" todos
    todos))

(defn get-items-left []
  (count (remove #(:done (val %)) @todos)))

(defn todos-completed []
  (count (filter #(:done (val %)) @todos)))

(defn remove-all-completed-todo []
  (reset! todos (into {} (remove #(:done (val %)) @todos))))

