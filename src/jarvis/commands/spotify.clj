(ns jarvis.commands.spotify
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(def app "Spotify")

(defn get-album [query]
  (-> "http://ws.spotify.com/search/1/album.json"
      (http/get {:query-params {:q query} :as :json})
      (:body)
      :albums
      first))

(defn play-album! [query]
  (if-let [{:keys [href]} (get-album query)]
    (osa/do! (str "play track \"" href "\"") app)
    (speech/say! "album not found.")))

(def commands
  [{:cmd ["spotify" "play"]     :fn (fn [_] (osa/do! "play" app))}
   {:cmd ["spotify" "stop"]     :fn (fn [_] (osa/do! "pause" app))}
   {:cmd ["spotify" "next"]     :fn (fn [_] (osa/do! "next track" app))}
   {:cmd ["spotify" "previous"] :fn (fn [_] (osa/do! "previous track" app))}
   {:cmd ["spotify" "album"]    :fn (fn [ws] (play-album! (str/join " " ws)))}])
