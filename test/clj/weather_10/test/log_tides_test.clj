(ns weather-10.test.log-tides-test
  (:require [clojure.test :as t :refer [is deftest testing]]
            [weather-10.log-tides :as sut]))

(deftest get-world-tide-reading-should-return-valid-data
       (let [tides (sut/get-worldtide-data ["51.317" "0.057"])]
         (is (map? tides))
         (is (vector? (:extremes tides)))
         (is  (string? (:date (first (:extremes tides)))))
         (is  (string? (:type (first (:extremes tides)))))))
