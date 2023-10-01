(ns component.complete-item-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [freeport.core :refer [get-free-port!]]
   [service.system :as system])
  (:import
   [com.microsoft.playwright Playwright]
   [com.microsoft.playwright.options AriaRole]))

(defn setup [scrapper port]
  (let [browser (.launch (.chromium scrapper))
        page (.newPage browser)]
    (.navigate page (str "http://localhost:" port))
    page))

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
    (let [port (get-free-port!)
          sut (system/start {:port port})]
      (try
        (with-open [playwright (Playwright/create)]
          (let [page (setup playwright port)]
            (is (= "Htmx + Clojure" (.title page)))))
        (finally (system/stop sut))))))

(deftest complete-test
  (testing "Home Page Test"
    (let [port (get-free-port!)
          sut (system/start {:port port
                             :db (sorted-map 1 {:id 1
                                                :name "buy milk"
                                                :done false})})]
      (try
        (with-open [playwright (Playwright/create)]
          (let [page (setup playwright port)
                task (-> page
                         (.locator "#todo-list")
                         (.getByRole AriaRole/LISTITEM)
                         (.first)
                         (.getByRole AriaRole/CHECKBOX))]
            (.click task)
            (is (true? (.isChecked task)))))
        (finally (system/stop sut))))))

(deftest undo-test
  (testing "Home Page Test"
    (let [port (get-free-port!)
          sut (system/start {:port port
                             :db (sorted-map 1 {:id 1
                                                :name "buy milk"
                                                :done true})})]
      (try
        (with-open [playwright (Playwright/create)]
          (let [page (setup playwright port)
                task (-> page
                         (.locator "#todo-list")
                         (.getByRole AriaRole/LISTITEM)
                         (.first)
                         (.getByRole AriaRole/CHECKBOX))]
            (.click task)
            (is (false? (.isChecked task)))))
        (finally (system/stop sut))))))
