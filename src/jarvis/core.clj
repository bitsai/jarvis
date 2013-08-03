(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.speech :as speech]))

(def commands
  (concat basic/commands
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
