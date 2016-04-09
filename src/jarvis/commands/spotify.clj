(ns jarvis.commands.spotify
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [jarvis.osascript :as osa])
  (:import [javax.xml.bind DatatypeConverter]))

(defn- base64-encode [s]
  (-> s (.getBytes) (DatatypeConverter/printBase64Binary)))

(defn- get-token! []
  (let [auth (->> (format "%s:%s"
                          (-> env :spotify-client-id)
                          (-> env :spotify-client-secret))
                  (base64-encode)
                  (format "Basic %s"))]
    (-> "https://accounts.spotify.com/api/token"
        (http/post {:form-params {:grant_type "client_credentials"}
                    :headers {"Authorization" auth}
                    :as :json})
        (:body)
        (:access_token))))

(defn- get-playlists! []
  (-> (format "https://api.spotify.com/v1/users/%s/playlists"
              (-> env :spotify-user-id))
      (http/get {:headers {"Authorization" (format "Bearer %s" (get-token!))}
                 :as :json})
      (:body)
      (:items)))

(defn- album-available? [a]
  (let [country (-> env :spotify-country (or "US"))]
    (->> a :availability :territories (re-find (re-pattern country)))))

(defn- track-available? [t]
  (-> t :album album-available?))

(defn- search! [category s available?]
  (-> (format "http://ws.spotify.com/search/1/%s.json" category)
      (http/get {:query-params {:q s}
                 :as :json})
      (:body)
      (get (keyword (str category "s")))
      (->> (filter available?))))

(defn- find! [category available?]
  (fn [s]
    (if-let [items (seq (search! category s available?))]
      (for [i items]
        (format "%s [%s]"
                (-> i :name)
                (-> i :artists first :name)))
      "no items found")))

(defn find-playlist! [_]
  (if-let [items (seq (get-playlists!))]
    (->> items
         (map :name)
         (str/join "\n"))
    "no playlists found"))

(defn play-playlist! [input]
  (if-let [item (->> (get-playlists!)
                     (filter #(-> % :name str/lower-case (= input)))
                     (first))]
    (osa/tell! "Spotify" (format "play track \"%s\"" (:uri item)))
    "no playlists found"))

(defn find-album! [s]
  (find! "album" album-available?))

(defn play-album! [s]
  (if-let [album (first (search! "album" s album-available?))]
    (osa/tell! "Spotify" (format "play track \"%s\"" (:href album)))
    "album not found"))

(defn find-track! [s]
  (find! "track" track-available?))

(defn play-track! [s]
  (if-let [track (first (search! "track" s track-available?))]
    (osa/tell! "Spotify" (format "play track \"%s\" in context \"%s\""
                                 (-> track :href)
                                 (-> track :album :href)))
    "track not found"))

(defn tell! [s]
  (fn [_] (osa/tell! "Spotify" s)))
