(ns component.complete-item-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [freeport.core :refer [get-free-port!]]
   [service.system :as system])
  (:import
   [com.microsoft.playwright Playwright]
   [com.microsoft.playwright.options AriaRole]))

(defn setup
  ([test] (setup test {}))
  ([test overrides]
   (let [port (get-free-port!)
         defaults {:port port
                   :db (sorted-map)}
         sut (system/start (merge defaults overrides))]
     (try
       (with-open [playwright (Playwright/create)]
         (let [browser (.launch (.chromium playwright))
               page (.newPage browser)]
           (.navigate page (str "http://localhost:" port))
           (test page)))
       (finally (system/stop sut))))))

(require 'hashp.core)

(defn d [locator]
  #p (.evaluate locator "x => x.outerHTML"))

(defn ds [locator]
  #p
   (map
    identity
    (.evaluateAll locator "locators => locators.map(x => x.outerHTML)")))

(deftest title-test
  (testing "Home Page Test"
    (setup (fn [page]
             (is (= "Htmx + Clojure" (.title page)))))))

(deftest complete-test
  (testing "Home Page Test"
    (setup (fn [page]
             (let [task (-> page
                            (.locator "#todo-list")
                            (.getByRole AriaRole/LISTITEM)
                            (.first)
                            (.getByRole AriaRole/CHECKBOX))]
               (.click task)
               (is (true? (.isChecked task)))))
           {:db (sorted-map 1 {:id 1
                               :name "buy milk"
                               :done false})})))

(deftest undo-test
  (testing "Home Page Test"
    (setup (fn [page]
             (let [task (-> page
                            (.locator "#todo-list")
                            (.getByRole AriaRole/LISTITEM)
                            (.first)
                            (.getByRole AriaRole/CHECKBOX))]
               (.click task)
               (is (false? (.isChecked task)))))
           {:db (sorted-map 1 {:id 1
                               :name "buy milk"
                               :done true})})))
