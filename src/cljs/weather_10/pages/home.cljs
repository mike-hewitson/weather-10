(ns weather-10.pages.home
  (:require [reagent.core     :as r]
            [re-frame.core    :as rf]
            [goog.string      :as gstring]
            [goog.string.format]
            [cljs-time.core   :as time]
            [cljs-time.format :as time.format]))


; TODO autorefresh data if older than 10 mins

(defonce date-format (time.format/formatter "HH:mm"))


(defonce date-time-format (time.format/formatter "yyyy:MM:dd HH:mm"))


(defn days-to-next-spring [age-of-moon]
  (- 14 (mod age-of-moon 14)))


(defn format-moon-phase-text [age-of-moon]
  "create text for moon phase"
  (let [next-moon-type (if (= (quot age-of-moon 14) 0)
                         "full moon"
                         "new moon")
        days-to-next-spring (days-to-next-spring age-of-moon)]
    (if-not (= days-to-next-spring 14)
      (str " - "
           days-to-next-spring
           (if (= days-to-next-spring 1)
             " day to "
             " days to ")
           next-moon-type))))


(defn create-reading-element [reading]
  [:div.row
   [:h4 (str (:location reading) " ")
    [:i {:class (str "wi " (:icon reading))}]
    " - "
    [:i {:class (str "wi " (:moon-phase-icon reading))}]]
   [:table.table
    [:tbody
     [:tr
      [:td "week"]
      [:td (:week-summary reading)]]
     [:tr
      [:td "day"]
      [:td (:day-summary reading)]]
     [:tr
      [:td "moon"]
      [:td
       (:phase-of-moon reading)
       (format-moon-phase-text (:age-of-moon reading))]]
     (if (:date reading)
       [:tr
        [:td "next tide"]
        [:td
         (:type reading) " "
         (gstring/format "%.1f" (:height reading)) " m at "
         (time.format/unparse date-format
                     (time/to-default-time-zone
                      (:date reading)))
         (if (= (days-to-next-spring (:age-of-moon reading)) 14)
           " - spring tide")]])
     [:tr
      [:td "sunrise/set"]
      [:td (time.format/unparse date-format
                                (time/to-default-time-zone
                                 (:sunrise reading)))
           " / "
           (time.format/unparse date-format
                                (time/to-default-time-zone
                                 (:sunset reading)))]]
     [:tr
      [:td "temp"]
      [:td
       (gstring/format "%.1f" (:temperature-min reading))
       " / "
       [:strong
        (gstring/format "%.1f" (:temperature reading))]
       " / "
       (gstring/format "%.1f" (:temperature-max reading)) " °C"]]
     [:tr
      [:td "wind"]
      [:td (gstring/format "%.1f" (:wind-speed reading))
       " km/hr - "
       (:wind-direction reading)]]]]])


(defn home-page []
  [:div.container-fluid
   (when-let [latest @(rf/subscribe [:latest])]
     (print "------\n" latest "\n---------")
                                        ; [:div.container-fluid
     [:div {:class "row row-content"}
      [:div.col-xs-12
       [:ul {:class "tab-pane fade in active"}
        (for [reading (:readings latest)]
          ^{:key (:location reading)} [create-reading-element reading])]]
      [:div.col-xs-12
       [:time "weather info @ "
        (time.format/unparse
         date-time-format
         (time/to-default-time-zone
          (:date latest)))]]])])

; TODO only show required locations as per database config
