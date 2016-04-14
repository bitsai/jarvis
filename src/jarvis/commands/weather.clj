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
   :temperature (parse-data #"temperature \| (.*)\n" weather)
   :conditions (parse-data #"conditions \| (.*)\n" weather) 
   :wind (parse-data #"wind speed \| (.*)\n" weather)
   :forecast-today (parse-data #"Today\n(.*)\n" weather)})

(defn announce! [location]
  ;; run in future so we load weather while saying the greeting
  (future (basic/say! (format "Hello, it's %s." (->local-time))))
  (let [weather (wolfram/ask! (format "weather near %s" location)
                              ;; only get the pods we need
                              {:podindex "2,3"})]
    ;; run in future so we don't block on text-to-speech
    (future (if (= weather "no results found")
              (basic/say! weather)
              (let [parsed-weather (parse-weather weather)]
                (basic/say! (format "The weather in %s is %s, %s."
                                    (:location parsed-weather)
                                    (:temperature parsed-weather)
                                    (:conditions parsed-weather)))
                (when-let [wind (:wind parsed-weather)]
                  (basic/say! (format "With winds at %s."
                                      wind)))
                (basic/say! (format "Today's temperature will be %s."
                                    (:forecast-today parsed-weather))))))
    weather))
