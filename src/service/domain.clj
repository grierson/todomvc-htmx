(ns service.domain)

(defn filtered-todo [todos filter-name]
  (case filter-name
    "active" (remove #(:done (val %)) todos)
    "completed" (filter #(:done (val %)) todos)
    "all" todos
    todos))

(defn get-items-left [todos]
  (count (remove #(:done (val %)) todos)))

(defn todos-completed [todos]
  (count (filter #(:done (val %)) todos)))
