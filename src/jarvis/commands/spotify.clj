(ns jarvis.commands.spotify
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn do! [cmd]
  (fn [_] (osa/do! "Spotify" cmd)))

(defn get-item [item query]
  (-> (str "http://ws.spotify.com/search/1/" item ".json")
      (http/get {:query-params {:q query} :as :json})
      (:body)
      (get (keyword (str item "s")))
      (first)))

(defn play-album! [words]
  (if-let [{:keys [href]} (get-item "album" (str/join " " words))]
    (osa/do! "Spotify" (format "play track \"%s\"" href))
    (speech/say! (str "album not found."))))

(defn play-track! [words]
  (if-let [{:keys [album href]} (get-item "track" (str/join " " words))]
    (do
      (osa/do! "Spotify" (format "play track \"%s\" in context \"%s\""
                                 href
                                 (:href album)))
      ;; HACK to fix repeat being disabled when playing a track
      (Thread/sleep 1000)
      (osa/do! "Spotify" "set repeating to false")
      (osa/do! "Spotify" "set repeating to true"))
    (speech/say! (str "track not found."))))

(def commands
  [{:cmd ["spotify" "album"]    :fn play-album!}
   {:cmd ["spotify" "next"]     :fn (do! "next track")}
   {:cmd ["spotify" "previous"] :fn (do! "previous track")}
   {:cmd ["spotify" "quit"]     :fn (do! "quit")}
   {:cmd ["spotify" "resume"]   :fn (do! "play")}
   {:cmd ["spotify" "stop"]     :fn (do! "pause")}
   {:cmd ["spotify" "track"]    :fn play-track!}])
