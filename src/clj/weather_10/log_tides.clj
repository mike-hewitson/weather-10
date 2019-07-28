(ns weather-10.log-tides
  (:require [clj-http.client       :as client]
            [clojure.tools.logging :as log]
            [cprop.core            :as cprop :refer [load-config]]
            [cprop.source          :as source]
            [monger.core           :as mg]
            [monger.collection     :as mc]))

; TODO move locations to database

(def tide-locations
  '(["Paradise Beach" ["-34.0521" "24.5412"]]
    ;["Salt River" ["-33.9263" "18.4545"]]
    ))
    ; ["London" ["51.317" "0.057"]]))


(defn get-worldtide-data [[lat lon]]
  "retrieve a set of readings from worldtide.com for a gps location"
  (let [my-url (str
                "https://www.worldtides.info/api?extremes&lat="
                lat
                "&lon="
                lon
                "&key=9521f994-2075-4c9f-824e-dc4c85fabd8a")]
    (:body (client/get my-url {:as :json}))))


(defn build-tides []
  (map (fn [[location gps]]
         {:location location
          :tides (get-worldtide-data gps)})
       tide-locations))


(defonce now
  (new java.util.Date))


(defn log-tides []
  (let [uri (:database-url
             (cprop/load-config :merge
                                [(source/from-system-props)
                                 (source/from-env)]))
        db (:db (mg/connect-via-uri uri))
        tides (build-tides)
        _ (log/info tides)]
    (mc/insert db "tides" {:date now :locations (build-tides)})))


(defn -main []
  (log-tides)
  (log/info "Logged one set of tides")
  (System/exit 0))
