(ns service.routes
  (:require
   [muuntaja.core :as m]
   [reitit.coercion.malli :as mcoercion]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as rrc]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [service.domain :as domain]
   [service.ui :as ui]
   [hiccup2.core :as h]
   [service.database :as database]))

(defn render [handler & [status]]
  {:status (or status 200)
   :body (str (h/html handler))})

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

(defn app-index [db {:keys [parameters headers]}]
  (let [filter (get-in parameters [:query :filter])
        ajax-request? (get headers "hx-request")
        todos (database/get-todos db)]
    (if (and filter ajax-request?)
      (render (list (ui/todo-list (domain/filtered-todo todos filter))
                    (ui/todo-filters filter)))
      (render (ui/template todos filter)))))

(defn add-item [db {:keys [parameters]}]
  (let [name (get-in parameters [:form :todo])
        todo (add-todo! db name)
        todos (database/get-todos db)]
    (render (list (ui/todo-item (val (last todo)))
                  (ui/item-count todos)))))

(defn edit-item [db {:keys [parameters]}]
  (let [id (get-in parameters [:path :id])
        {:keys [id name]} (database/get-todo db id)]
    (render (ui/todo-edit id name))))

(defn update-item [db {:keys [parameters]}]
  (let [id (get-in parameters [:path :id])
        name (get-in parameters [:form :name])
        _ (update-todo! db id name)]
    (render (ui/todo-item id))))

(defn patch-item [db {:keys [parameters]}]
  (let [id (get-in parameters [:path :id])
        _ (toggle-todo! db id)
        todo-item (database/get-todo db id)
        todos (database/get-todos db)]
    (render (list (ui/todo-item todo-item)
                  (ui/item-count todos)
                  (ui/clear-completed-button todos)))))

(defn delete-item [db {:keys [parameters]}]
  (remove-todo! db (get-in parameters [:path :id]))
  (render (ui/item-count (database/get-todos db))))

(defn clear-completed [db _request]
  (remove-all-completed-todo db)
  (let [todos (database/get-todos db)]
    (render (list (ui/todo-list todos)
                  (ui/item-count todos)
                  (ui/clear-completed-button todos)))))

(defn app
  [{:keys [db]}]
  (ring/ring-handler
   (ring/router
    [["/" {:get {:parameters {:query [:map [:filter {:optional true} string?]]}
                 :handler (partial app-index db)}}]
     ["/todos" {:delete {:handler (partial clear-completed db)}
                :post {:parameters {:form [:map [:todo string?]]}
                       :handler (partial add-item db)}}]
     ["/todos/update/:id" {:parameters {:path [:map [:id int?]]
                                        :form [:map [:name string?]]}
                           :patch {:handler (partial update-item db)}}]
     ["/todos/:id" {:parameters {:path [:map [:id int?]]}
                    :delete {:handler (partial delete-item db)}
                    :patch {:handler  (partial patch-item db)}}]
     ["/todos/edit/:id" {:parameters {:path [:map [:id int?]]}
                         :get {:handler  (partial edit-item db)}}]]
    {:conflicts nil
     :data {:coercion   mcoercion/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware
                         muuntaja/format-request-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-negotiate-middleware]}})))
