(ns component.complete-item-test
  (:require [clojure.test :refer [deftest is testing]]
            [service.system :as system])
  (:import [com.microsoft.playwright Playwright]))

(defn setup [scrapper port]
  (let [browser (.launch (.chromium scrapper))
        page (.newPage browser)]
    (.navigate page (str "http://localhost:" port))
    page))

(deftest home-page-test
  (testing "Home Page Test"
    (let [port 4000
          sut (system/start {:port port})]
      (try
        (with-open [playwright (Playwright/create)]
          (let [page (setup playwright port)]
            (is (= "Htmx + Clojure" (.title page)))))
        (finally (system/stop sut))))))


