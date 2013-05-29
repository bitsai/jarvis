(ns jarvis.commands.weather
  (:require [clj-http.client :as http]
            [clj-time.core :as time]
            [clj-time.format :as time-fmt]
            [clj-time.local :as time-local]
            [clojure.string :as str]
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
  (-> x (- 273.15) (* (/ 9 5)) (+ 32) int (str " degrees")))

(defn mps->MPH [x]
  (-> x (* 2.23694) int (str " mph")))

(defn forecast [id]
  (let [fs (-> "http://api.openweathermap.org/data/2.5/forecast"
               (http/get {:query-params {:id id} :as :json})
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
     :id (-> w :id)
     :name (-> w :name)
     :temp (-> w :main :temp K->F)
     :wind (-> w :wind :speed mps->MPH)}))

(defn announce! [city]
  (let [{:keys [desc humidity id name temp wind]} (weather city)
        {:keys [high low]} (forecast id)
        line1 "the weather in %s is %s with %s."
        line2 "the humidity is %s with %s winds."
        line3 "sunrise will be %s and sunset will be %s."
        line4 "the 24-hour forecast is high of %s and low of %s."]
    (announce-time!)
    (speech/say! (format line1 name temp desc))
    (speech/say! (format line2 humidity wind))
    ;; (speech/say! (format line3 sunrise sunset))
    (speech/say! (format line4 high low))))

(def commands
  [{:cmd ["weather"] :fn (fn [ws] (announce! (str/join " " ws)))}])
