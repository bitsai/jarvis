(ns jarvis.commands.spotify
  (:require [jarvis.commands.util :as util]
            [jarvis.speech :as speech]))

(def app "Spotify")

(defn set-volume [vol]
  (try
    (util/osa app (str "set sound volume to " (Integer. vol)))
    (catch Exception e
      (speech/say! "volume should be an integer."))))

(def commands
  [{:cmd ["spotify" "play"]     :fn (fn [_] (util/osa app "play"))}
   {:cmd ["spotify" "pause"]    :fn (fn [_] (util/osa app "pause"))}
   {:cmd ["spotify" "next"]     :fn (fn [_] (util/osa app "next track"))}
   {:cmd ["spotify" "previous"] :fn (fn [_] (util/osa app "previous track"))}
   {:cmd ["spotify" "volume"]   :fn (fn [ws] (set-volume (first ws)))}])
