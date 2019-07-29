(ns weather-10.services.home
  (:require [clojure.math.numeric-tower :as math]
            [weather-10.db.core          :as db]
            [clj-time.core              :as time]
            [clj-time.coerce            :as coerce]))

(def fields-needed
  [:sunset
   :day-summary
   :wind-speed
   :sunrise
   :icon
   :wind-bearing
   :wind-direction
   :temperature-max
   :temperature-min
   :location
   :temperature
   :week-summary])

                                        ; TODO get locations from database

(def locations-to-send
  ["Paradise Beach"])

#_(def locations-to-send
  ["Paradise Beach" "Salt River"])

(def wind-directions
  ["Northerly" "North-easterly" "Easterly" "South-easterly"
   "Southerly" "South-westerley" "Westerly" "North-westerly"])


(def moon-icons-transform
  ["wi-moon-alt-new"
   "wi-moon-alt-waxing-crescent-1"
   "wi-moon-alt-waxing-crescent-2"
   "wi-moon-alt-waxing-crescent-3"
   "wi-moon-alt-waxing-crescent-4"
   "wi-moon-alt-waxing-crescent-5"
   "wi-moon-alt-waxing-crescent-6"
   "wi-moon-alt-first-quarter"
   "wi-moon-alt-waxing-gibbous-1"
   "wi-moon-alt-waxing-gibbous-2"
   "wi-moon-alt-waxing-gibbous-3"
   "wi-moon-alt-waxing-gibbous-4"
   "wi-moon-alt-waxing-gibbous-5"
   "wi-moon-alt-waxing-gibbous-6"
   "wi-moon-alt-full"
   "wi-moon-alt-waning-gibbous-1"
   "wi-moon-alt-waning-gibbous-2"
   "wi-moon-alt-waning-gibbous-3"
   "wi-moon-alt-waning-gibbous-4"
   "wi-moon-alt-waning-gibbous-5"
   "wi-moon-alt-waning-gibbous-6"
   "wi-moon-alt-first-quarter"
   "wi-moon-alt-waning-crescent-1"
   "wi-moon-alt-waning-crescent-2"
   "wi-moon-alt-waning-crescent-3"
   "wi-moon-alt-waning-crescent-4"
   "wi-moon-alt-waning-crescent-5"
   "wi-moon-alt-waning-crescent-6"])


(defn translate-direction [bearing]
  "translate wind bearing to direction in text"
  (wind-directions (mod (math/round (/ bearing 45)) 8)))


(defn format-readings-for-merge [{readings :readings}]
  "create map of selected reading data for merge"
  (apply merge
         (map (fn [{location :location :as reading}]
                {location (select-keys reading fields-needed)})
              readings)))


(defn create-directions-for-merge [{readings :readings}]
  "create wind directions for merging"
  (apply merge
         (map (fn [{:keys [location wind-bearing]}]
                {location {:wind-direction
                           (translate-direction wind-bearing)}})
              readings)))


(defn create-tides-for-merge [{locations :locations}]
  "Create a map with the next tide with key of location"
  (let [now (coerce/from-date (new java.util.Date))]
    (apply merge (map (fn [{:keys [location tides]}]
                        {location
                         (some #(if (time/after?
                                     (coerce/from-date (coerce/to-date
                                                        (:date %)))
                                     now)
                                  %)
                               (:extremes tides))})
                      locations))))

                                        ; TODO create test for moon phases

(defn normalise-age [age-of-moon]
  "make age of moon usaeble for indexing"
  (cond
    (> age-of-moon 27) 27
    (= age-of-moon 0)  0
    :else              (dec age-of-moon)))

                                        ; TODO chat to Rob about the function below, does not feel right

(defn create-moonphase-for-merge [{readings :readings}]
  "strip out and transform age of moon to icon, age and phase"
  (apply merge (map (fn [{location :location :as reading}]
                      {location
                       {:moon-phase-icon
                        (-> reading
                            :moon-phase
                            (* 28)
                            int
                            normalise-age
                            moon-icons-transform)
                        :age-of-moon
                        (-> reading
                            :moon-phase
                            (* 28)
                            int
                            normalise-age)
                        :phase-of-moon "some crap"
                        #_(:phaseofMoon "some crap"
                                      #_(:moon-phase reading))}})
                    ;;TODO: fix this

                    readings)))


(defn prepare-home-page-data []
  "bring together all of the home page data components"
  (let [weather-data (first (db/get-latest))
        tides-data   (first (db/get-tides))
 ;       moon-data    (first (db/get-moonphases))
        reading-date (:date weather-data)]
    {:date reading-date
     :readings
     (vals
      (select-keys
       (merge-with merge
                   (format-readings-for-merge weather-data)
                   (create-directions-for-merge weather-data)
                   (create-tides-for-merge tides-data)
                   (create-moonphase-for-merge weather-data))
       locations-to-send))}))

(comment

  (prepare-home-page-data)
  (format-readings-for-merge (first (db/get-latest)))
  (create-moonphase-for-merge (first (db/get-latest)))
 )
