(ns jarvis.commands.spotify
  (:require [clojure.java.shell :as shell]
            [jarvis.speech :as speech]))

(defn tell-spotify [s]
  (let [cmd (str "tell application \"Spotify\" to " s)]
    (shell/sh "osascript" "-e" cmd)))

(defn set-volume [vol]
  (try
    (tell-spotify (str "set sound volume to " (Integer. vol)))
    (catch Exception e
      (speech/say! "volume should be an integer."))))

(def commands
  [{:cmd ["spotify" "play"]     :fn (fn [_] (tell-spotify "play"))}
   {:cmd ["spotify" "pause"]    :fn (fn [_] (tell-spotify "pause"))}
   {:cmd ["spotify" "next"]     :fn (fn [_] (tell-spotify "next track"))}
   {:cmd ["spotify" "previous"] :fn (fn [_] (tell-spotify "previous track"))}
   {:cmd ["spotify" "volume"]   :fn (fn [ws] (set-volume (first ws)))}])
