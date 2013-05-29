(ns jarvis.commands.spotify
  (:require [jarvis.osa :as osa]))

(def app "Spotify")

(def commands
  [{:cmd ["spotify" "play"]     :fn (fn [_] (osa/do! "play" app))}
   {:cmd ["spotify" "stop"]     :fn (fn [_] (osa/do! "pause" app))}
   {:cmd ["spotify" "next"]     :fn (fn [_] (osa/do! "next track" app))}
   {:cmd ["spotify" "previous"] :fn (fn [_] (osa/do! "previous track" app))}])
