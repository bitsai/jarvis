(ns jarvis.commands.spotify
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn do! [cmd]
  (fn [_] (osa/do! "Spotify" cmd)))

(defn get-album [query]
  (-> "http://ws.spotify.com/search/1/album.json"
      (http/get {:query-params {:q query} :as :json})
      (:body)
      (:albums)
      (->> (filter #(->> % :availability :territories (re-find #"US"))))
      (first)))

(defn play-album! [words]
  (if-let [{:keys [href]} (get-album (str/join " " words))]
    (osa/do! "Spotify" (str "play track \"" href "\""))
    (speech/say! "album not found.")))

(def commands
  [{:cmd ["spotify" "album"]    :fn play-album!}
   {:cmd ["spotify" "next"]     :fn (do! "next track")}
   {:cmd ["spotify" "previous"] :fn (do! "previous track")}
   {:cmd ["spotify" "quit"]     :fn (do! "quit")}
   {:cmd ["spotify" "resume"]   :fn (do! "play")}
   {:cmd ["spotify" "stop"]     :fn (do! "pause")}])
