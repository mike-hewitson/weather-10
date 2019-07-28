(ns weather-10.services.history
  (:require [weather-10.db.core :as db]
            [clj-time.core     :as time]
            [clj-time.coerce   :as coerce]
            [clj-time.periodic :as periodic]))

                                        ; TODO make resolution a parameter
                                        ; TODO days back to be a parameter on the from-end (default 7)
                                        ; TODO get locations from database


(def locations-to-send
  ["Paradise Beach"])


(def fields-needed
  [:location
   :wind-speed
   :temperature])


(def points-to-plot 200)


(defn create-history-seq [days-back end-point]
  "create a sequence of 50 dates between a date and today"
  (let [interval  (int (/ (* days-back 24  3600) (dec points-to-plot)))
        from-date (time/minus end-point (time/days days-back))]
    (map coerce/to-date
         (take points-to-plot (periodic/periodic-seq from-date
                                                     (time/seconds interval))))))


(defn create-readings-list [dates]
  "create a list of readings, on for each date "
  (map #(first (db/get-reading-at-time %))
       dates))


(defn create-display-list [readings-list]
  "format the a reeading list for the html template "
  (map (fn [{:keys [readings date]}]
         (let [location (:reading (first readings))]
           {:date     date
            :location location}))
       readings-list))


(defn extract-fields-for-one-reading [{date :date readings :readings}]
  (map (fn [x] (merge {:date date} (select-keys x fields-needed)))
       readings))


(defn rebuild-data-per-location [all-data]
  (map (fn [x] {:location x
               :history  (filter #(= (:location %) x)
                                 all-data)})
       locations-to-send))


(defn prepare-history-data []
  "bring together all of the home page data components"
  (->> (create-history-seq 7 (time/now))
       create-readings-list
       dedupe
       (map extract-fields-for-one-reading)
       flatten
       rebuild-data-per-location))

                                        ; TODO refactor code
                                        ; TODO create tests
