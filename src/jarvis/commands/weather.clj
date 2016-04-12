(ns jarvis.commands.weather
  (:require [jarvis.commands.basic :as basic]
            [jarvis.commands.wolfram :as wolfram])
  (:import [java.text SimpleDateFormat]
           [java.util Date]))

(defn- ->local-time []
  (.format (SimpleDateFormat. "hh:mm a") (Date.)))

(defn- parse-data [re s]
  (->> s (re-find re) second))

(defn- parse-weather [weather]
  {:location (parse-data #"Weather forecast for (.*)\n" weather)
   :temperature (parse-data #"temperature \| (.*)\s+\(wind" weather)
   :conditions (parse-data #"conditions \| (.*)\n" weather) 
   :wind (parse-data #"wind speed \| (.*)\n" weather)
   :forecast-today (parse-data #"Today\n(.*)\n" weather)})

(defn announce! [_]
  (let [weather (wolfram/ask! "weather near me")]
    (basic/say! (format "Hello, it's %s." (->local-time)))
    (if (= weather "no results found")
      (basic/say! weather)
      (let [parsed-weather (parse-weather weather)]
        (basic/say! (format "The weather in %s is %s, %s, with winds at %s."
                            (:location parsed-weather)
                            (:temperature parsed-weather)
                            (:conditions parsed-weather)
                            (:wind parsed-weather)))
        (basic/say! (format "Today's temperature will be %s."
                            (:forecast-today parsed-weather)))))
    weather))
