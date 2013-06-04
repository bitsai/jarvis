(ns jarvis.commands.dvd
  (:require [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(def app "DVD player")

(defn set-fullscreen! [word]
  (case word
    "on"  (osa/do! "set viewer full screen to true" app)
    "off" (osa/do! "set viewer full screen to false" app)
    (speech/say! "please say on or off.")))

(defn set-subtitle! [word]
  (case word
    "on"  (osa/do! "set subtitle to 1" app)
    "off" (osa/do! "set subtitle to 0" app)
    (speech/say! "please say on or off.")))

(def commands
  [{:cmd ["dvd" "play"]       :fn (fn [_] (osa/do! "play dvd" app))}
   {:cmd ["dvd" "stop"]       :fn (fn [_] (osa/do! "pause dvd" app))}
   {:cmd ["dvd" "next"]       :fn (fn [_] (osa/do! "play next chapter" app))}
   {:cmd ["dvd" "previous"]   :fn (fn [_] (osa/do! "play previous chapter" app))}
   {:cmd ["dvd" "quit"]       :fn (fn [_] (osa/do! "quit" app))}
   {:cmd ["dvd" "eject"]      :fn (fn [_] (osa/do! "eject dvd" app))}
   {:cmd ["dvd" "fullscreen"] :fn (fn [ws] (set-fullscreen! (first ws)))}
   {:cmd ["dvd" "subtitle"]   :fn (fn [ws] (set-subtitle! (first ws)))}])
