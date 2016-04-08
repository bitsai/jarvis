(ns jarvis.commands.core
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]
            [jarvis.osascript :as osa]))

(def commands
  [{:prefix ["print"]       :f basic/print}
   {:prefix ["say"]         :f basic/say}
   {:prefix ["screensaver"] :f basic/start-screensaver}
   {:prefix ["volume"]      :f basic/set-volume}
   {:prefix ["spotify" "next"]      :f (spotify/tell "next track")}
   {:prefix ["spotify" "previous"]  :f (spotify/tell "previous track")}
   {:prefix ["spotify" "start"]     :f (spotify/tell "play")}
   {:prefix ["spotify" "stop"]      :f (spotify/tell "pause")}
   {:prefix ["spotify" "album"]     :f spotify/play-album}
   {:prefix ["spotify" "albums"]    :f spotify/query-albums}
   {:prefix ["spotify" "playlists"] :f spotify/user-playlists}
   {:prefix ["spotify" "track"]     :f spotify/play-track}
   {:prefix ["spotify" "tracks"]    :f spotify/query-tracks}])

(defn match? [tokens {:keys [prefix] :as cmd}]
  (->> tokens
       (map str/lower-case)
       (take (count prefix))
       (= prefix)))

(defn process [s]
  (let [tokens (str/split s #"\s+")]
    (if-let [{:keys [prefix f]} (->> commands
                                     (filter #(match? tokens %))
                                     (first))]
      (f (->> tokens (drop (count prefix)) (str/join " ")))
      (wolfram/ask s))))
