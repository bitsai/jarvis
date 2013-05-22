(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.weather :as weather]
            [jarvis.speech :as speech]))

(def command-fns
  {"print"   (fn [args] (apply println args))
   "say"     (fn [args] (speech/say! (str/join " " args)))
   "weather" (fn [args] (weather/announce! (str/join " " args)))})

(defn process! [[cmd & args]]
  (when-let [f (get command-fns cmd)]
    (f args)))
