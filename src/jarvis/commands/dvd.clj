(ns jarvis.commands.dvd
  (:require [jarvis.commands.util :as util]))

(def app "DVD player")

(def commands
  [{:cmd ["dvd" "play"]  :fn (fn [_] (util/osa app "play dvd"))}
   {:cmd ["dvd" "stop"]  :fn (fn [_] (util/osa app "pause dvd"))}
   {:cmd ["dvd" "eject"] :fn (fn [_] (util/osa app "eject dvd"))}
   {:cmd ["dvd" "quit"]  :fn (fn [_] (util/osa app "quit"))}])
