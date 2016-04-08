(ns jarvis.commands.spotify
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [environ.core :as e]
            [jarvis.osascript :as osa]))

(defn base64-encode [s]
  (javax.xml.bind.DatatypeConverter/printBase64Binary (.getBytes s)))

(defn get-token [& [client-id client-secret]]
  (let [client-id (or client-id (-> e/env :spotify :client-id))
        client-secret (or client-secret (-> e/env :spotify :client-secret))
        auth (->> (str client-id ":" client-secret)
                  (base64-encode)
                  (format "Basic %s"))]
    (-> "https://accounts.spotify.com/api/token"
        (http/post {:form-params {:grant_type "client_credentials"}
                    :headers {"Authorization" auth}})
        (deref)
        (:body)
        (json/parse-string true)
        (:access_token))))

(defn user-playlists [& [user]]
  (let [user (or user (-> e/env :spotify :user))
        auth (format "Bearer %s" (get-token))
        items (-> "https://api.spotify.com/v1/users/%s/playlists"
                  (format user)
                  (http/get {:headers {"Authorization" auth}})
                  (deref)
                  (:body)
                  (json/parse-string true)
                  (:items))]
    (if (seq items)
      (for [i items]
        (format "%s<br>%s" (:name i) (:href i)))
      "no playlists found")))

(defn search [category s available?]
  (-> (format "http://ws.spotify.com/search/1/%s.json" category)
      (http/get {:query-params {:q s}})
      (deref)
      (:body)
      (json/parse-string true)
      (get (keyword (str category "s")))
      (->> (filter available?))))

(defn album-available? [a]
  (let [country (-> e/env :spotify :country)]
    (->> a :availability :territories (re-find (re-pattern country)))))

(defn track-available? [t]
  (-> t :album album-available?))

(defn play-album [s]
  (if-let [album (first (search "album" s album-available?))]
    (osa/tell "Spotify" (format "play track \"%s\"" (:href album)))
    "album not found"))

(defn play-track [s]
  (if-let [track (first (search "track" s track-available?))]
    (osa/tell "Spotify" (format "play track \"%s\" in context \"%s\""
                                (-> track :href)
                                (-> track :album :href)))
    "track not found"))

(defn query [category available?]
  (fn [s]
    (if-let [items (seq (search category s available?))]
      (for [i items]
        (format "%s [%s]"
                (-> i :name)
                (-> i :artists first :name)))
      "no items found")))

(defn tell-spotify [s]
  (fn [_] (osa/tell "Spotify" s)))

(def commands
  [{:prefix ["spotify" "next"]      :f (tell-spotify "next track")}
   {:prefix ["spotify" "previous"]  :f (tell-spotify "previous track")}
   {:prefix ["spotify" "start"]     :f (tell-spotify "play")}
   {:prefix ["spotify" "stop"]      :f (tell-spotify "pause")}
   {:prefix ["spotify" "album"]     :f play-album}
   {:prefix ["spotify" "albums"]    :f (query "album" album-available?)}
   {:prefix ["spotify" "playlists"] :f user-playlists}
   {:prefix ["spotify" "track"]     :f play-track}
   {:prefix ["spotify" "tracks"]    :f (query "track" track-available?)}])
