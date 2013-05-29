(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn set-volume [vol]
  (try
    (osa/do! (str "set volume output volume " (Integer. vol)))
    (catch Exception e
      (speech/say! "volume should be a number between 0 and 100."))))

(def commands
  (concat
   [{:cmd ["print"]  :fn (fn [ws] (println (str/join " " ws)))}
    {:cmd ["say"]    :fn (fn [ws] (speech/say! (str/join  " " ws)))}
    {:cmd ["volume"] :fn (fn [ws] (set-volume (first ws)))}]
   dvd/commands
   spotify/commands
   weather/commands))

(defn matches? [cmd words]
  (let [n (count cmd)]
    (= cmd (take n words))))

(defn process! [words]
  (let [lowered (map str/lower-case words)]
    (if-let [match (->> commands
                        (filter #(-> % :cmd (matches? lowered)))
                        (first))]
      (let [n (count (:cmd match))]
        ((:fn match) (drop n lowered)))
      (speech/say! "I don't know that command."))))
