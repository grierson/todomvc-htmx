(ns service.database)

(defn get-todos [db] @db)

(defn get-todo [db id] (get @db id))

(defn add-todo! [db id name]
  (swap! db assoc id {:id id :name name :done false}))

(defn update-todo! [db id name]
  (swap! db assoc-in [(Integer. id) :name] name))

(defn toggle-todo! [db id]
  (swap! db update-in [id :done] not))

(defn remove-todo! [db id]
  (swap! db dissoc (Integer. id)))

(defn remove-all-complete [db]
  (reset! db (into {} (remove #(:done (val %)) (get-todos db)))))
