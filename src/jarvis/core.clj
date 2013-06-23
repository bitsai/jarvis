(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn set-volume! [[vol]]
  (try
    (osa/do! (str "set volume output volume " (Integer. vol)))
    (catch Exception e
      (speech/say! "volume should be an integer between 0 and 100."))))

(defn start-screensaver! [_]
  (osa/do! "System Events" "start current screen saver"))

(def commands
  (concat
   [{:cmd ["print"]       :fn #(println (str/join " " %))}
    {:cmd ["say"]         :fn #(speech/say! (str/join  " " %))}
    {:cmd ["screensaver"] :fn start-screensaver!}
    {:cmd ["volume"]      :fn set-volume!}]
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
