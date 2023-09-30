(ns service.ui
  (:require [hiccup2.core :as h]
            [service.domain :as domain]))

(defn todo-item
  [{:keys [id name done]}]
  [:li {:id (str "todo-" id)
        :class (when done "completed")}
   [:div.view
    [:input.toggle {:hx-patch (str "/todos/" id)
                    :type "checkbox"
                    :checked done
                    :hx-target (str "#todo-" id)
                    :hx-swap "outerHTML"}]
    [:label {:hx-get (str "/todos/edit/" id)
             :hx-target (str "#todo-" id)
             :hx-swap "outerHTML"} name]
    [:button.destroy {:hx-delete (str "/todos/" id)
                      :_ (str "on htmx:afterOnLoad remove #todo-" id)}]]])

(defn todo-list
  [todos]
  (for [todo todos]
    (todo-item (val todo))))

(defn todo-edit
  [id name]
  [:form {:hx-patch (str "/todos/update/" id)}
   [:input.edit {:type "text"
                 :name "name"
                 :value name}]])

(defn item-count []
  (let [items-left (domain/get-items-left)]
    [:span#todo-count.todo-count {:hx-swap-oob "true"}
     [:strong items-left]]))

(defn todo-filters
  [filter]
  [:ul#filters.filters {:hx-swap-oob "true"}
   [:li [:a {:hx-get "/?filter=all"
             :hx-push-url "true"
             :hx-target "#todo-list"
             :class (when (= filter "all") "selected")} "All"]]
   [:li [:a {:hx-get "/?filter=active"
             :hx-push-url "true"
             :hx-target "#todo-list"
             :class (when (= filter "active") "selected")} "Active"]]
   [:li [:a {:hx-get "/?filter=completed"
             :hx-push-url "true"
             :hx-target "#todo-list"
             :class (when (= filter "completed") "selected")} "Completed"]]])

(defn clear-completed-button
  []
  [:button#clear-completed.clear-completed
   {:hx-delete "/todos"
    :hx-target "#todo-list"
    :hx-swap-oob "true"
    :hx-push-url "/"
    :class (when-not (pos? (domain/todos-completed)) "hidden")}
   "Clear completed"])

(defn template
  [filter]
  (h/html
   {:mode :html}
   [:html
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Htmx + Clojure"]
     [:link {:href "https://unpkg.com/todomvc-app-css@2.4.2/index.css" :rel "stylesheet"}]
     [:script {:src "https://unpkg.com/htmx.org@1.9.6/dist/htmx.min.js" :defer true}]
     [:script {:src "https://unpkg.com/hyperscript.org@0.9.11/dist/_hyperscript.min.js" :defer true}]]
    [:body
     [:section.todoapp
      [:header.header
       [:h1 "todos"]
       [:form
        {:hx-post "/todos"
         :hx-target "#todo-list"
         :hx-swap "beforeend"
         :_ "on htmx:afterOnLoad set #txtTodo.value to ''"}
        [:input#txtTodo.new-todo
         {:name "todo"
          :placeholder "What needs to be done?"
          :autofocus ""}]]]
      [:section.main
       [:input#toggle-all.toggle-all {:type "checkbox"}]
       [:label {:for "toggle-all"} "Mark all as complete"]]
      [:ul#todo-list.todo-list
       (todo-list (domain/filtered-todo filter @domain/todos))]
      [:footer.footer
       (item-count)
       (todo-filters filter)
       (clear-completed-button)]]
     [:footer.info
      [:p "Click to edit a todo"]
      [:p "Created by "
       [:a {:href "https://github.com/grierson"} "Kyle Grierson"]]
      [:p "Part of "
       [:a {:href "http://todomvc.com"} "TodoMVC"]]]]]))
