(ns jarvis.commands.spotify
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [jarvis.util :as util]
            [org.httpkit.client :as http]))

(def country "US")

(defn album-available? [a]
  (->> a :availability :territories (re-find (re-pattern country))))

(defn track-available? [t]
  (-> t :album album-available?))

(defn query [category s available?]
  (-> (format "http://ws.spotify.com/search/1/%s.json" category)
      (http/get {:query-params {:q s}})
      (deref)
      (:body)
      (json/read-str :key-fn keyword)
      (get (keyword (str category "s")))
      (->> (filter available?))))

(defn play-album [s]
  (if-let [album (first (query "album" s album-available?))]
    (util/tell "Spotify" (format "play track \"%s\"" (:href album)))
    "album not found"))

(defn play-track [s]
  (if-let [track (first (query "track" s track-available?))]
    (do
      (util/tell "Spotify" (format "play track \"%s\" in context \"%s\""
                                  (-> track :href)
                                  (-> track :album :href)))
      ;; HACK to fix repeat being disabled when playing a track
      (Thread/sleep 1000)
      (util/tell "Spotify" "set repeating to false")
      (util/tell "Spotify" "set repeating to true"))
    "track not found"))

(defn search [category available?]
  (fn [s]
    (if-let [items (seq (query category s available?))]
      (->> (for [i (take 5 items)]
             (format "%s [%s]"
                     (-> i :name)
                     (-> i :artists first :name)))
           (str/join "\n\n"))
      "no items found")))

(defn tell-spotify [s]
  (fn [_] (util/tell "Spotify" s)))

(def commands
  [{:prefix "spotify album"    :fun play-album}
   {:prefix "spotify albums"   :fun (search "album" album-available?)}
   {:prefix "spotify next"     :fun (tell-spotify "next track")}
   {:prefix "spotify previous" :fun (tell-spotify "previous track")}
   {:prefix "spotify start"    :fun (tell-spotify "play")}
   {:prefix "spotify stop"     :fun (tell-spotify "pause")}
   {:prefix "spotify track"    :fun play-track}
   {:prefix "spotify tracks"   :fun (search "track" track-available?)}])
