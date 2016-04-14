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
  (-> (format "https://api.spotify.com/v1/users/%s/playlists"
              user-id)
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
  (-> (format "https://api.spotify.com/v1/albums/%s/tracks"
              album-id)
      (http/get {:query-params {:market (-> env :spotify-country (or "US"))}
                 :as :json})
      (:body)
      (:items)))

;; public fns

(defn my-playlists! []
  (if-let [items (-> env :spotify-user-id get-playlists! seq)]
    (map :name items)
    ["no playlists found"]))

(defn view-playlist! [input]
  (if-let [item (get-playlist! (:spotify-user-id env) input)]
    (->> (get-playlist-tracks! (-> item :owner :id) (:id item))
         (map-indexed #(format "%d. %s" (inc %1) (-> %2 :track :name))))
    ["playlist not found"]))

(defn play-playlist! [input]
  (if-let [item (get-playlist! (:spotify-user-id env) input)]
    [(osa/tell! "Spotify" (format "play track \"%s\"" (:uri item)))]
    ["playlist not found"]))

(defn find-album! [input]
  (if-let [items (->> input (search! "album") seq)]
    (map :name items)
    ["no albums found"]))

(defn view-album! [input]
  (if-let [item (->> input (search! "album") first)]
    (->> (get-album-tracks! (:id item))
         (map-indexed #(format "%d. %s" (inc %1) (:name %2))))
    ["album not found"]))

(defn play-album! [input]
  (if-let [item (->> input (search! "album") first)]
    [(osa/tell! "Spotify" (format "play track \"%s\"" (:uri item)))]
    ["album not found"]))

(defn run! [s]
  (fn []
    [(osa/tell! "Spotify" s)]))
