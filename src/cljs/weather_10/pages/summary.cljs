(ns weather-10.pages.summary
  (:require [re-frame.core    :as rf]
            [reagent.core     :as r]
            [cljs-time.core   :as time]
            [cljs-time.format :as time.format]
            [cljs-time.coerce :as time.coerce]))

                                        ; TODO create tests for this stuff

(def date-format (time.format/formatter "yyyy/MM/dd"))


(defn round [x]
  (/ (Math/round (* x 10)) 10))

(enable-console-print!)


(defonce chart-config-static
  {:chart       {:type "spline"}
   :yAxis       {:min    0
                 :title  {:text  "Degrees C"
                          :align "high"}
                 :labels {:overflow "justify"}}
   :tooltip     {:valueSuffix " °C"}
   :plotOptions {:spline {:dataLabels {:enabled false}
                          :marker     {:enabled false}}}
   :exporting   {:enabled false}
   :credits     {:enabled false}})


(defn build-series [summary-data]
  {:series
   [{:name "Maximum"
     :data (map (fn [x]
                  [(time.coerce/to-long (time/to-default-time-zone (:date x)))
                   (round (:max-temp x))])
                (:summary summary-data))}
    {:name "Average"
     :data (map (fn [x]
                  [(time.coerce/to-long (time/to-default-time-zone (:date x)))
                   (round (:avg-temp x))])
                (:summary summary-data))}
    {:name "Minimum"
     :data (map (fn [x]
                  [(time.coerce/to-long (time/to-default-time-zone (:date x)))
                   (round (:min-temp x))])
                (:summary summary-data))}]})


(defn build-xaxis [summary-data]
  {:xAxis {:type  "datetime"
           :dateTimeLabelFormats
           {:month "%e %b"
            :year  "%b"}
           :title {:text "Date"}}})


(defn build-title [summary-data]
  {:title {:text (str (:location summary-data) " Temperature Summary")}})


(defn build-chart-config [summary-data]
  (merge (build-xaxis summary-data)
         (build-series summary-data)
         (build-title summary-data)
         chart-config-static))


(defn home-render []
  [:div {:style {:min-width "310px" :max-width "800px"
                 :height    "400px" :margin    "0 auto"}}])


(defn extract-data [location]
                                        ; (rf/dispatch-sync [:get-summary])
  (first (filter #(= location (:location %)) @(rf/subscribe [:summary]))))


(defn home-did-mount [location this]
  (js/Highcharts.Chart. (r/dom-node this) (clj->js
                                           (build-chart-config
                                            (extract-data location)))))


(defn chart [location]
  (r/create-class {:reagent-render      home-render
                   :component-did-mount (partial home-did-mount location)}))


(defn summary-page []
  (let [show-twirly (rf/subscribe [:show-twirly])]
    (if @show-twirly
      [:div
       [:h2 "Loading ......."]]
      [:div
       [chart "Paradise Beach"]
       ;[chart "Sandton"]
       ;[chart "Salt River"]
       ])))

                                        ; TODO get locations to show from database

