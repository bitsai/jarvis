(ns jarvis.core
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.speech :as speech]))

(def commands
  (concat
   [{:cmd ["print"] :fn (fn [ws] (println (apply str ws)))}
    {:cmd ["say"]   :fn (fn [ws] (speech/say! (apply str ws)))}]
   spotify/commands
   weather/commands))

(defn matches? [cmd words]
  (let [n (count cmd)]
    (= cmd (take n words))))

(defn process! [words]
  (if-let [match (->> commands
                      (filter #(-> % :cmd (matches? words)))
                      first)]
    (let [n (cound (:cmd match))]
      ((:fn match) (drop n words)))
    (speech/say! "I don't know that command.")))
