(ns weather-10.services.summary
  (:require [weather-10.db.core :as db]))

                                        ; TODO get locations from database


(def locations-to-send
  ["Paradise Beach"])


(defn build-for-one-location [data]
  (map (fn [x] (dissoc (merge (:_id x) x) :_id)) data))


(defn prepare-summary-data []
  (map (fn [x] {:location x
               :summary  (build-for-one-location (db/get-summary x))})
       locations-to-send))

                                        ; TODO add tests
