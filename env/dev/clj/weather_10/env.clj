(ns weather-10.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [weather-10.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[weather-10 started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[weather-10 has shut down successfully]=-"))
   :middleware wrap-dev})
