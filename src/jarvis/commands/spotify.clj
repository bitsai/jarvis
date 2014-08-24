(ns jarvis.commands.spotify
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [environ.core :as e]
            [jarvis.osascript :as osa]
            [org.httpkit.client :as http]))

(defn search [category s available?]
  (-> (format "http://ws.spotify.com/search/1/%s.json" category)
      (http/get {:query-params {:q s}})
      (deref)
      (:body)
      (json/read-str :key-fn keyword)
      (get (keyword (str category "s")))
      (->> (filter available?))))

(defn album-available? [a]
  (->> a :availability :territories (re-find (re-pattern (e/env :country)))))

(defn track-available? [t]
  (-> t :album album-available?))

(defn play-album [s]
  (if-let [album (first (search "album" s album-available?))]
    (osa/tell "Spotify" (format "play track \"%s\"" (:href album)))
    "album not found"))

(defn play-track [s]
  (if-let [track (first (search "track" s track-available?))]
    (do
      (osa/tell "Spotify" (format "play track \"%s\" in context \"%s\""
                                  (-> track :href)
                                  (-> track :album :href)))
      ;; HACK to fix repeat being disabled when playing a track
      (Thread/sleep 1000)
      (osa/tell "Spotify" "set repeating to false")
      (osa/tell "Spotify" "set repeating to true"))
    "track not found"))

(defn query [category available?]
  (fn [s]
    (if-let [items (seq (search category s available?))]
      (->> (for [i (take 5 items)]
             (format "%s [%s]"
                     (-> i :name)
                     (-> i :artists first :name)))
           (str/join "\n\n"))
      "no items found")))

(defn tell-spotify [s]
  (fn [_] (osa/tell "Spotify" s)))

(def commands
  [{:prefix ["spotify" "next"]     :f (tell-spotify "next track")}
   {:prefix ["spotify" "previous"] :f (tell-spotify "previous track")}
   {:prefix ["spotify" "start"]    :f (tell-spotify "play")}
   {:prefix ["spotify" "stop"]     :f (tell-spotify "pause")}
   {:prefix ["spotify" "album"]    :f play-album}
   {:prefix ["spotify" "albums"]   :f (query "album" album-available?)}
   {:prefix ["spotify" "track"]    :f play-track}
   {:prefix ["spotify" "tracks"]   :f (query "track" track-available?)}])
