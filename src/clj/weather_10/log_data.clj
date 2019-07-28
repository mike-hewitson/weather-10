(ns weather-10.log-data
  (:require [clj-http.client            :as client]
            [clojure.tools.logging      :as log]
            [cprop.core                 :as cprop :refer [load-config]]
            [cprop.source               :as source]
            [clojure.math.numeric-tower :as math]
            [monger.core                :as mg]
            [monger.collection          :as mc]))

(def icons-transform
  {"day-sunny" "wi-day-sunny"
   "clear-night" "wi-night-clear"
   "rain" "wi-rain"
   "wind" "wi-strong-wind"
   "snow" "wi-snow"
   "sleet" "wi-sleet"
   "strong-wind" "wi-wind"
   "fog" "wi-fog"
   "cloudy" "wi-cloudy"
   "day-cloudy" "wi-day-cloudy"
   "partly-cloudy-day" "wi-day-sunny-overcast"
   "night-cloudy" "wi-night-cloudy"
   "partly-cloudy-night" "wi-night-partly-cloudy"
   "hail" "wi-hail"
   "thunderstorm" "wi-thunderstorm"
   "tornado" "wi-tornado"
   "clear-day" "wi-day-sunny"});


(defn float-and-round [x]
  "cast to float after rounding to one decimal"
  (float (/ (math/round (* x 10)) 10)))


(def reading-names
  [[["week-summary"]
    ["moon-phase" float]
    ["temperature-max" float-and-round]
    ["temperature-min" float-and-round]
    ["sunrise" (fn [x] (java.util.Date. (* 1000 x)))]
    ["sunset" (fn [x] (java.util.Date. (* 1000 x)))]
    ["day-summary"]
    ["now-summary"]
    ["icon" icons-transform]
    ["temperature" float-and-round]
    ["wind-speed" (fn [x] (float-and-round (* x 3.6)))]
    ["wind-bearing" long]
    ["pressure" float-and-round]
    ["humidity" float]
    ["precip-probability" float]
    ["precip-intensity" float]
    ["cloud-cover" float]]
   [[:daily :summary]
    [:data :moonPhase]
    [:data :temperatureMax]
    [:data :temperatureMin]
    [:data :sunriseTime]
    [:data :sunsetTime]
    [:data :summary]
    [:currently :summary]
    [:currently :icon]
    [:currently :temperature]
    [:currently :windSpeed]
    [:currently :windBearing]
    [:currently :pressure]
    [:currently :humidity]
    [:currently :precipProbability]
    [:currently :precipIntensity]
    [:currently :cloudCover]]])

; TODO move location to database

(def data-locations
  '(["Sandton" "-26.097,28.053"]
    ; ["Paradise Beach" "-34.0521,24.5412"]
    ; ["Salt River" "-33.9263,18.4545"]
    ))
    ; ["London" "51.317,0.057"]))

; Latitude: -33° 55' 34.79" S
; Longitude: 18° 27' 16.19" E
; -33.926329628 18.454498182


(defonce now
  (new java.util.Date))


(defn get-darksky-data [gps]
  "retrieve a set of readings from darksky.io for a gps location"
  (let [my-url
        (str
         "https://api.darksky.net/forecast/62888a9ff1907377b60a866701cf3338/"
         gps
         "?units=si&exclude=minutely,hourly,alerts,flags")]
    (:body (client/get my-url {:as :json}))))


(defn extract-reading-data [{:keys [daily currently]}]
  "extract the required data elements from the darksky message body"
  (zipmap (first reading-names)
          (map (fn [[k v]]
                 (let [data (v (first (:data daily)))]
                   (case k
                     :daily (v daily)
                     :data data
                     :currently (v currently)
                     nil)))
               (last reading-names))))


; TODO this feels wrong, need to refactor
(defn create-update [location reading-map]
  "create a hash that contains data for a location"
  (assoc (apply merge (map (fn [[[name & [my-cast]] value]]
                             {name (if my-cast
                                     (my-cast value)
                                     value)})
                           reading-map))
         "location" location))


(defn build-readings []
  (map (fn [[location gps]]
         (->> (get-darksky-data gps)
              (extract-reading-data)
              (create-update location)))
       data-locations))


(defn log-readings []
  (let [uri (:database-url
             (cprop/load-config :merge
                          [(source/from-system-props)
                           (source/from-env)]))
        db (:db (mg/connect-via-uri uri))]
    (mc/insert db "readings" {:date now :readings (build-readings)})))


(defn -main []
  (log-readings)
  (log/info "Logged one set of readings")
  (System/exit 0))

(comment
  (extract-reading-data (get-darksky-data "-26.097,28.053"))
  )
