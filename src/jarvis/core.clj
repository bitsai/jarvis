(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.speech :as speech]
            [jarvis.weather :as weather]))

(def command-fns
  {"print"   (fn [args] (apply println args))
   "say"     (fn [args] (speech/say! (str/join " " args)))
   "weather" (fn [args] (weather/announce! (str/join " " args)))})

(defn process! [[cmd & args]]
  (when-let [f (get command-fns cmd)]
    (f args)))
