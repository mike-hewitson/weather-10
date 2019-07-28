(ns weather-10.test.log-data-test
  (:require [clojure.test :as t :refer [is deftest testing]]
            [weather-10.log-data :as sut]
            [weather-10.test.fixtures :as fix]))

(deftest get-darksky-reading-should-return-data
  (let [reading (sut/get-darksky-data "51.317,0.057")]
    (is  (map? reading))
    (is  (map? (:currently reading)))
    (is  (map? (:currently reading)))
    (is  (map? (:daily reading)))))

(deftest extract-reading-data-should-work
  (let [reading-data (sut/extract-reading-data fix/a-darksky-reading-body)]
    (is (map? reading-data))
    (is (= "Foggy in the evening." (get reading-data ["day-summary"])))
    (is (="Partly Cloudy" (get reading-data ["now-summary"])))))

(deftest create-update-should-work
  (let [update (sut/create-update "London"
                                  (sut/extract-reading-data
                                   fix/a-darksky-reading-body))]
    (is (map? update))
    (is (= 18 (count update)))
    (is (inst? (get
                update "sunrise"))))
  (:icon update))
