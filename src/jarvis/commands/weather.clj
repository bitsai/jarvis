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
  {:location (parse-data #"Latest recorded weather for (.*)\n" weather)
   :temperature (parse-data #"temperature \| (\S+ \S+)" weather)
   :conditions (parse-data #"conditions \| (.*)\n" weather)
   :humidity (parse-data #"relative humidity \| (\S+)" weather)
   :wind (parse-data #"wind speed \| (\S+ \S+)" weather)})

(defn- parse-forecast [forecast]
  {:today (parse-data #"Today\n(.*)\n" forecast)})

(defn announce! [location]
  ;; run in future so we load weather while saying the greeting
  (future (basic/say! (format "Hello, it's %s." (->local-time))))
  (let [outputs (wolfram/ask! (format "weather near %s" location)
                              ;; only get the pods we need
                              {:podindex "2,3"})]
    ;; run in future so we don't block on text-to-speech
    (future (if (-> outputs first (= "no results found"))
              (basic/say! (first outputs))
              (let [parsed-weather (-> outputs first parse-weather)
                    parsed-forecast (-> outputs second parse-forecast)]
                (basic/say! (format "The weather in %s is %s, %s."
                                    (:location parsed-weather)
                                    (:temperature parsed-weather)
                                    (:conditions parsed-weather)))
                (when-let [humidity (:humidity parsed-weather)]
                  (basic/say! (format "The humidity is %s." humidity)))
                (when-let [wind (:wind parsed-weather)]
                  (basic/say! (format "With %s winds." wind)))
                (basic/say! (format "Today's temperature will be %s."
                                    (:today parsed-forecast))))))
    outputs))
