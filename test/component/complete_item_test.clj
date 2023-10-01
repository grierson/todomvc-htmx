(ns component.complete-item-test
  (:require [clojure.test :refer [deftest is testing]]
            [service.system :as system])
  (:import [com.microsoft.playwright Playwright]))

(defn setup [scrapper]
  (let [browser (.launch (.chromium scrapper))
        page (.newPage browser)]
    (.navigate page "http://localhost:3000")
    page))

(deftest home-page-test
  (testing "Home Page Test"
    (let [sut (system/start)]
      (try
        (with-open [playwright (Playwright/create)]
          (let [page (setup playwright)]
            (is (= "Htmx + Clojure" (.title page)))))
        (finally (system/stop sut))))))


