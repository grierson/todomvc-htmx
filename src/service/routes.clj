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
   [hiccup2.core :as h]))

(defn render [handler & [status]]
  {:status (or status 200)
   :body (str (h/html handler))})

(defn app-index [{:keys [parameters headers]}]
  (let [filter (get-in parameters [:query :filter])
        ajax-request? (get headers "hx-request")]
    (if (and filter ajax-request?)
      (render (list (ui/todo-list (domain/filtered-todo filter @domain/todos))
                    (ui/todo-filters filter)))
      (render (ui/template filter)))))

(defn add-item [{:keys [parameters]}]
  (let [name (get-in parameters [:form :todo])
        todo (domain/add-todo! name)]
    (render (list (ui/todo-item (val (last todo)))
                  (ui/item-count)))))

(defn edit-item [{:keys [parameters]}]
  (let [id (get-in parameters [:path :id])
        {:keys [id name]} (get @domain/todos id)]
    (render (ui/todo-edit id name))))

(defn update-item [{:keys [parameters]}]
  (let [id (get-in parameters [:path :id])
        name (get-in parameters [:form :name])
        todo (domain/update-todo! id name)]
    (render (ui/todo-item (get todo id)))))

(defn patch-item [{:keys [parameters]}]
  (let [id (get-in parameters [:path :id])
        _ (domain/toggle-todo! id)
        todo-item (get @domain/todos id)]
    (render (list (ui/todo-item todo-item)
                  (ui/item-count)
                  (ui/clear-completed-button)))))

(defn delete-item [{:keys [parameters]}]
  (domain/remove-todo! (get-in parameters [:path :id]))
  (render (ui/item-count)))

(defn clear-completed [_]
  (domain/remove-all-completed-todo)
  (render (list (ui/todo-list @domain/todos)
                (ui/item-count)
                (ui/clear-completed-button))))

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get {:parameters {:query [:map [:filter {:optional true} string?]]}
                 :handler app-index}}]
     ["/todos" {:delete {:handler clear-completed}
                :post {:parameters {:form [:map [:todo string?]]}
                       :handler add-item}}]
     ["/todos/update/:id" {:parameters {:path [:map [:id int?]]
                                        :form [:map [:name string?]]}
                           :patch {:handler update-item}}]
     ["/todos/:id" {:parameters {:path [:map [:id int?]]}
                    :delete {:handler delete-item}
                    :patch {:handler  patch-item}}]
     ["/todos/edit/:id" {:parameters {:path [:map [:id int?]]}
                         :get {:handler  edit-item}}]]
    {:conflicts nil
     :data {:coercion   mcoercion/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware
                         muuntaja/format-request-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-negotiate-middleware]}})))
