(ns jarvis.commands.spotify
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn do! [cmd]
  (osa/do! "Spotify" cmd))

(defn get-album [query]
  (-> "http://ws.spotify.com/search/1/album.json"
      (http/get {:query-params {:q query} :as :json})
      (:body)
      (:albums)
      (first)))

(defn play-album! [query]
  (if-let [{:keys [href]} (get-album query)]
    (do! (str "play track \"" href "\""))
    (speech/say! "album not found.")))

(def commands
  [{:cmd ["spotify" "play"]     :fn (fn [_] (do! "play"))}
   {:cmd ["spotify" "stop"]     :fn (fn [_] (do! "pause"))}
   {:cmd ["spotify" "next"]     :fn (fn [_] (do! "next track"))}
   {:cmd ["spotify" "previous"] :fn (fn [_] (do! "previous track"))}
   {:cmd ["spotify" "quit"]     :fn (fn [_] (do! "quit"))}
   {:cmd ["spotify" "album"]    :fn (fn [ws] (play-album! (str/join " " ws)))}])
