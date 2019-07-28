(ns weather-10.app
  (:require [weather-10.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
