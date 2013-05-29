(ns jarvis.commands.weather
  (:require [clj-http.client :as http]
            [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]
            [clj-time.format :as time-fmt]
            [clojure.string :as str]
            [jarvis.speech :as speech]))

(defn K->F [x]
  (-> x (- 273.15) (* (/ 9 5)) (+ 32) int (str " degrees")))

(defn mps->MPH [x]
  (-> x (* 2.23694) int (str " mph")))

(defn UTC->tz [x tz]
  (-> x
      (time-coerce/to-date-time)
      (time/to-time-zone tz)
      (->> (time-fmt/unparse (time-fmt/formatter-local "hh:mm a")))))

(defn forecast [city]
  (let [fs (-> "http://api.openweathermap.org/data/2.5/forecast"
               (http/get {:query-params {:q city} :as :json})
               (:body)
               (:list))
        temps (->> fs
                   (take 8) ;; take next 24 hours
                   (map #(-> % :main :temp)))]
    {:high (->> temps (apply max) K->F)
     :low (->> temps (apply min) K->F)}))

(defn weather [city]
  (let [w (-> "http://api.openweathermap.org/data/2.5/weather"
              (http/get {:query-params {:q city} :as :json})
              (:body))]
    {:desc (-> w :weather first :description)
     :humidity (-> w :main :humidity (str "%"))
     :name (-> w :name)
     :sunrise (-> w :sys :sunrise (* 1000))
     :sunset (-> w :sys :sunset (* 1000))
     :temp (-> w :main :temp K->F)
     :wind (-> w :wind :speed mps->MPH)}))

(defn announce! [weather forecast]
  (let [{:keys [desc humidity name sunrise sunset temp wind]} weather
        {:keys [high low]} forecast
        tz (time/default-time-zone)
        line0 "greetings, it's %s."
        line1 "the weather in %s is %s with %s."
        line2 "the humidity is %s with %s winds."
        line3 "sunrise will be %s and sunset will be %s."
        line4 "the 24-hour forecast is high of %s and low of %s."]
    (speech/say! (format line0 (UTC->tz (time/now) tz)))
    (speech/say! (format line1 name temp desc))
    (speech/say! (format line2 humidity wind))
    (speech/say! (format line3 (UTC->tz sunrise tz) (UTC->tz sunset tz)))
    (speech/say! (format line4 high low))))

(def commands
  [{:cmd ["weather"] :fn (fn [ws] (let [city (str/join " " ws)
                                       w (weather city)
                                       f (forecast city)]
                                   (announce! w f)))}])
