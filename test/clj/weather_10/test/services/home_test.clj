(ns weather-10.test.services.home-test
  (:require [clojure.test :as t :refer [is deftest testing]]
            [weather-10.services.home :as sut]
            [weather-10.test.fixtures :as fix]))

(deftest translate-direction-should-return-correct-direction
  (is (= (sut/translate-direction 0) "Northerly"))
  (is (= (sut/translate-direction 45) "North-easterly"))
  (is (= (sut/translate-direction 44.9) "North-easterly"))
  (is (= (sut/translate-direction 50) "North-easterly"))
  (is (= (sut/translate-direction 359) "Northerly")))

(deftest format-reading-for-merge
       (let [readings (sut/format-readings-for-merge fix/latest-reading)]
         (is (map? readings))
         (is (= 3 (count readings)))
         (is (= 10 (count (val (first readings)))))))

(deftest create-directions-for-merge
       (let [directions (sut/create-directions-for-merge fix/latest-reading)]
         (is (map? directions))
         (is  (= 3 (count directions)))
         (is (= 1 (count (val (first directions)))))
         (is (:wind-direction (val (first directions))))
         (is (string? (:wind-direction (val (first directions)))))))

; TODO weird bug - function does t/after? does not work when tested

; (facts "about 'create-tide-for-merge'"
;   (let [tides (r/create-tide-for-merge fix/tides-data)]
;     (fact "it should return a map"
;      (map? tides) => true)
;     (fact "it should contain 2 items"
;      (count tides) => 2)
;     (fact "elements should contain 4 items"
;      (count (val (first tides))) => 4)
;     (fact "it should contain a date field"
;       (:date (val (first tides))) => truthy)
;     (fact "it should contain some correct data"
;       (string? (:type (val (first tides)))) => true)))
