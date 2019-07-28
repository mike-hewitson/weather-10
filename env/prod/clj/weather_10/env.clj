(ns weather-10.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[weather-10 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[weather-10 has shut down successfully]=-"))
   :middleware identity})
