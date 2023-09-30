(ns component.complete-item-test
  (:require [clojure.test :refer [deftest is testing]])
  (:import (org.htmlunit WebClient)))

(deftest home-page-test
  (testing "Home Page Test"
    (let [web-client (WebClient.)]
      (try
        (let [page (.getPage web-client "http://localhost:3000/")]
          (is (= "failing test" (.getTitleText page))))
        (finally
          (.close web-client))))))
