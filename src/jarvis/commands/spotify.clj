(ns jarvis.commands.spotify
  (:refer-clojure :exclude [run!])
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [jarvis.osascript :as osa])
  (:import [javax.xml.bind DatatypeConverter]))

;; private helpers

(defn- base64-encode [s]
  (-> s (.getBytes) (DatatypeConverter/printBase64Binary)))

(defn- get-token! []
  (let [auth (->> (format "%s:%s"
                          (:spotify-client-id env)
                          (:spotify-client-secret env))
                  (base64-encode)
                  (format "Basic %s"))]
    (-> "https://accounts.spotify.com/api/token"
        (http/post {:form-params {:grant_type "client_credentials"}
                    :headers {"Authorization" auth}
                    :as :json})
        (:body)
        (:access_token))))

(defn- get-playlists! [user-id]
  (-> (format "https://api.spotify.com/v1/users/%s/playlists" user-id)
      (http/get {:headers {"Authorization" (format "Bearer %s" (get-token!))}
                 :as :json})
      (:body)
      (:items)))

(defn- get-playlist! [user-id playlist-name]
  (->> (get-playlists! user-id)
       (filter #(-> % :name str/lower-case (= playlist-name)))
       (first)))

(defn- get-playlist-tracks! [user-id playlist-id]
  (-> (format "https://api.spotify.com/v1/users/%s/playlists/%s/tracks"
              user-id
              playlist-id)
      (http/get {:headers {"Authorization" (format "Bearer %s" (get-token!))}
                 :as :json})
      (:body)
      (:items)))

(defn- search! [category input]
  (-> "https://api.spotify.com/v1/search"
      (http/get {:query-params {:market (-> env :spotify-country (or "US"))
                                :q input
                                :type category}
                 :as :json})
      (:body)
      (get (keyword (str category "s")))
      (:items)))

(defn- get-album-tracks! [album-id]
  (-> (format "https://api.spotify.com/v1/albums/%s/tracks" album-id)
      (http/get {:query-params {:market (-> env :spotify-country (or "US"))}
                 :as :json})
      (:body)
      (:items)))

;; public fns

(defn my-playlists! []
  (if-let [playlists (-> env :spotify-user-id get-playlists! seq)]
    (map :name playlists)
    ["no playlists found"]))

(defn view-playlist! [input]
  (if-let [playlist (get-playlist! (:spotify-user-id env) input)]
    (->> (get-playlist-tracks! (-> playlist :owner :id) (:id playlist))
         (map-indexed #(format "%d. %s" (inc %1) (-> %2 :track :name))))
    ["playlist not found"]))

(defn play-playlist! [input & [track-idx]]
  (if-let [playlist (get-playlist! (:spotify-user-id env) input)]
    (let [idx (-> track-idx (or "1") (Long.) dec)
          track (-> (get-playlist-tracks! (-> playlist :owner :id) (:id playlist))
                    (nth idx))]
      [(osa/tell! "Spotify" (format "play track \"%s\" in context \"%s\""
                                    (-> track :track :uri)
                                    (:uri playlist)))])
    ["playlist not found"]))

(defn find-album! [input]
  (if-let [albums (->> input (search! "album") seq)]
    (map :name albums)
    ["no albums found"]))

(defn view-album! [input]
  (if-let [album (->> input (search! "album") first)]
    (->> (get-album-tracks! (:id album))
         (map-indexed #(format "%d. %s" (inc %1) (:name %2))))
    ["album not found"]))

(defn play-album! [input & [track-idx]]
  (if-let [album (->> input (search! "album") first)]
    (let [idx (-> track-idx (or "1") (Long.) dec)
          track (-> (get-album-tracks! (:id album))
                    (nth idx))]
      [(osa/tell! "Spotify" (format "play track \"%s\" in context \"%s\""
                                    (:uri track)
                                    (:uri album)))])
    ["album not found"]))

(defn run! [s]
  (fn []
    [(osa/tell! "Spotify" s)]))
