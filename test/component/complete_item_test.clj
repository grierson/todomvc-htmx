(ns component.complete-item-test
  (:require [clojure.test :refer [deftest is testing]]
            [service.system :as system]
            [freeport.core :refer [get-free-port!]])
  (:import [com.microsoft.playwright Playwright]
           [com.microsoft.playwright.assertions PlaywrightAssertions]
           [java.util.regex Pattern]))

(defn setup [scrapper port]
  (let [browser (.launch (.chromium scrapper))
        page (.newPage browser)]
    (.navigate page (str "http://localhost:" port))
    page))

(require 'hashp.core)

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
          sut (system/start {:port port})]
      (try
        (with-open [playwright (Playwright/create)]
          (let [page (setup playwright port)
                item (.locator page "text=Get Started")]
            (is (= "Htmx + Clojure" (.title page)))))
        (finally (system/stop sut))))))
