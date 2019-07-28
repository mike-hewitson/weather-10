(ns weather-10.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [weather-10.core-test]))

(doo-tests 'weather-10.core-test)

