(ns jarvis.commands.dvd
  (:require [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn do! [cmd]
  (osa/do! "DVD player" cmd))

(defn set-fullscreen! [word]
  (case word
    "on"  (do! "set viewer full screen to true")
    "off" (do! "set viewer full screen to false")
    (speech/say! "please say on or off.")))

(defn set-subtitle! [word]
  (case word
    "on"  (do! "set subtitle to 1")
    "off" (do! "set subtitle to 0")
    (speech/say! "please say on or off.")))

(def commands
  [{:cmd ["dvd" "play"]       :fn (fn [_] (do! "play dvd"))}
   {:cmd ["dvd" "stop"]       :fn (fn [_] (do! "pause dvd"))}
   {:cmd ["dvd" "next"]       :fn (fn [_] (do! "play next chapter"))}
   {:cmd ["dvd" "previous"]   :fn (fn [_] (do! "play previous chapter"))}
   {:cmd ["dvd" "quit"]       :fn (fn [_] (do! "quit"))}
   {:cmd ["dvd" "eject"]      :fn (fn [_] (do! "eject dvd"))}
   {:cmd ["dvd" "fullscreen"] :fn (fn [[w]] (set-fullscreen! w))}
   {:cmd ["dvd" "subtitle"]   :fn (fn [[w]] (set-subtitle! w))}])
