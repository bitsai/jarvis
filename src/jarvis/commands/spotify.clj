(ns jarvis.commands.spotify
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [jarvis.osascript :as osa]))

(defn search [category s]
  (-> (str "http://ws.spotify.com/search/1/" category ".json")
      (http/get {:query-params {:q s} :as :json})
      (:body)
      (get (keyword (str category "s")))))

(defn search-albums [s]
  (if-let [albums (seq (search "album" s))]
    (->> (for [[i a] (map list (range) (take 5 albums))]
           (str i ":" (:name a)))
         (str/join ", "))
    (throw (Exception. "No albums found."))))

(defn play-album [s]
  (if-let [album (first (search "album" s))]
    (osa/tell "Spotify" (str "play track \"" (:href album) "\""))
    (throw (Exception. "Album not found."))))

(defn play-track [s]
  (if-let [track (first (search "track" s))]
    (do
      (osa/tell "Spotify" (format "play track \"%s\" in context \"%s\""
                                  (-> track :href)
                                  (-> track :album :href)))
      ;; HACK to fix repeat being disabled when playing a track
      (Thread/sleep 1000)
      (osa/tell "Spotify" "set repeating to false")
      (osa/tell "Spotify" "set repeating to true"))
    (throw (Exception. "Track not found."))))

(defn tell-spotify [s]
  (fn [_] (osa/tell "Spotify" s)))

(def commands
  [{:prefix "spotify album"    :fn play-album}
   {:prefix "spotify artist"   :fn search-albums}
   {:prefix "spotify next"     :fn (tell-spotify "next track")}
   {:prefix "spotify previous" :fn (tell-spotify "previous track")}
   {:prefix "spotify quit"     :fn (tell-spotify "quit")}
   {:prefix "spotify start"    :fn (tell-spotify "play")}
   {:prefix "spotify stop"     :fn (tell-spotify "pause")}
   {:prefix "spotify track"    :fn play-track}])
