(ns jarvis.commands.dvd
  (:require [jarvis.osa :as osa]))

(def app "DVD player")

(def commands
  [{:cmd ["dvd" "play"]  :fn (fn [_] (osa/do! "play dvd" app))}
   {:cmd ["dvd" "stop"]  :fn (fn [_] (osa/do! "pause dvd" app))}
   {:cmd ["dvd" "eject"] :fn (fn [_] (osa/do! "eject dvd" app))}
   {:cmd ["dvd" "quit"]  :fn (fn [_] (osa/do! "quit" app))}])
