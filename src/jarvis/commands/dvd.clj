(ns jarvis.commands.dvd
  (:require [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn do! [cmd]
  (osa/do! "DVD player" cmd))

(defn set-audio! [word]
  (try
    (do! (str "set audio track to " (Integer. word)))
    (catch Exception e
      (speech/say! "audio track should be an integer."))))

(defn set-chapter! [word]
  (try
    (do! (str "set chapter to " (Integer. word)))
    (catch Exception e
      (speech/say! "chapter should be an integer."))))

(defn set-fullscreen! [word]
  (case word
    "on"  (do! "set viewer full screen to true")
    "off" (do! "set viewer full screen to false")
    (speech/say! "please say on or off.")))

(defn set-subtitle! [word]
  (try
    (do! (str "set subtitle to " (Integer. word)))
    (catch Exception e
      (speech/say! "subtitle should be an integer."))))

(def commands
  [{:cmd ["dvd" "audio"]      :fn (fn [[w]] (set-audio! w))}
   {:cmd ["dvd" "chapter"]    :fn (fn [[w]] (set-chapter! w))}
   {:cmd ["dvd" "eject"]      :fn (fn [_] (do! "eject dvd"))}
   {:cmd ["dvd" "fullscreen"] :fn (fn [[w]] (set-fullscreen! w))}
   {:cmd ["dvd" "play"]       :fn (fn [_] (do! "play dvd"))}
   {:cmd ["dvd" "quit"]       :fn (fn [_] (do! "quit"))}
   {:cmd ["dvd" "stop"]       :fn (fn [_] (do! "pause dvd"))}
   {:cmd ["dvd" "subtitle"]   :fn (fn [[w]] (set-subtitle! w))}])
