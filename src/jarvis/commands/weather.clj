(ns jarvis.commands.weather
  (:require [clj-http.client :as http]
            [clj-time.core :as time]
            [clj-time.format :as time-fmt]
            [clj-time.local :as time-local]
            [jarvis.speech :as speech]))

(defn announce-time! []
  (let [fmt (time-fmt/formatter-local "hh:mm a")
        now (time-local/local-now)
        time-str (time-fmt/unparse fmt now)]
    (cond
     (<= 0 (time/hour now) 11)
     (speech/say! (format "good morning. it's %s." time-str))
     (<= 12 (time/hour now) 4)
     (speech/say! (format "good afternoon. it's %s." time-str))
     :else
     (speech/say! (format "good evening. it's %s." time-str)))))

(defn K->F [x]
  (-> x (- 273.15) (* 1.8) (+ 32) int (str " degrees")))

(defn mps->MPH [x]
  (-> x (* 2.2369362920544) int (str " mph")))

(defn forecast [id]
  (let [fs (-> "http://api.openweathermap.org/data/2.5/forecast"
               (http/get {:query-params {:id id} :as :json})
               (:body)
               (:list))
        temps (->> fs
                   (take 8)  ;; take next 24 hours
                   (map #(-> % :main :temp)))]
    {:high (->> temps (apply max) K->F)
     :low (->> temps (apply min) K->F)}))

(defn weather [location]
  (let [w (-> "http://api.openweathermap.org/data/2.5/weather"
              (http/get {:query-params {:q location} :as :json})
              (:body))]
    {:condition (-> w :weather first :description)
     :humidity (-> w :main :humidity (str "%"))
     :id (-> w :id)
     :name (-> w :name)
     :temp (-> w :main :temp K->F)
     :wind (-> w :wind :speed mps->MPH)}))

(defn announce! [location]
  (let [{:keys [condition humidity id name temp wind]} (weather location)
        {:keys [high low]} (forecast id)
        line1 "the weather in %s is %s with %s."
        line2 "wind: %s. humidity: %s."
        line3 "24-hour forecast: high of %s and low of %s."]
    (announce-time!)
    (speech/say! (format line1 name temp condition))
    (speech/say! (format line2 wind humidity))
    (speech/say! (format line3 high low))))
