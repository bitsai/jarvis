(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.speech :as speech]))

(def commands
  (concat
   [{:cmd ["print"] :fn (fn [ws] (println (str/join " " ws)))}
    {:cmd ["say"]   :fn (fn [ws] (speech/say! (str/join  " " ws)))}]
   dvd/commands
   spotify/commands
   weather/commands))

(defn matches? [cmd words]
  (let [n (count cmd)]
    (= cmd (take n words))))

(defn process! [words]
  (if-let [match (->> commands
                      (filter #(-> % :cmd (matches? words)))
                      first)]
    (let [n (count (:cmd match))]
      ((:fn match) (drop n words)))
    (speech/say! "I don't know that command.")))
