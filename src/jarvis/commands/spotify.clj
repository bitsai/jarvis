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

(defn- search-playlists! [user-id playlist-name]
  (->> (get-playlists! user-id)
       (filter #(-> % :name str/lower-case (= playlist-name)))))

(defn- get-playlist-tracks! [playlist]
  (-> (format "https://api.spotify.com/v1/users/%s/playlists/%s/tracks"
              (-> playlist :owner :id)
              (:id playlist))
      (http/get {:headers {"Authorization" (format "Bearer %s" (get-token!))}
                 :as :json})
      (:body)
      (:items)
      ;; extract nested track data
      (-> (->> (map :track)))))

(defn- search! [category input]
  (-> "https://api.spotify.com/v1/search"
      (http/get {:query-params {:market (-> env :spotify-country (or "US"))
                                :q input
                                :type category}
                 :as :json})
      (:body)
      (get (keyword (str category "s")))
      (:items)))

(defn- get-album-tracks! [album]
  (-> (format "https://api.spotify.com/v1/albums/%s/tracks" (:id album))
      (http/get {:query-params {:market (-> env :spotify-country (or "US"))}
                 :as :json})
      (:body)
      (:items)))

(defn- find-items! [search-fn]
  (if-let [items (seq (search-fn))]
    (map :name items)
    ["no items found"]))

(defn- view-item-tracks! [search-fn get-tracks-fn]
  (if-let [item (first (search-fn))]
    (->> (get-tracks-fn item)
         (map-indexed #(format "%d. %s" (inc %1) (:name %2))))
    ["item not found"]))

(defn- play-item-track! [search-fn get-tracks-fn track-idx]
  (if-let [item (first (search-fn))]
    (let [idx (-> track-idx (or "1") (Long.) dec)
          track (-> (get-tracks-fn item)
                    (nth idx))]
      [(osa/tell! "Spotify" (format "play track \"%s\" in context \"%s\""
                                    (:uri track)
                                    (:uri item)))])
    ["item not found"]))

;; public fns

(defn my-playlists! []
  (find-items! #(get-playlists! (:spotify-user-id env))))

(defn view-my-playlist! [input]
  (view-item-tracks! #(search-playlists! (:spotify-user-id env) input)
                     get-playlist-tracks!))

(defn play-my-playlist! [input & [track-idx]]
  (play-item-track! #(search-playlists! (:spotify-user-id env) input)
                    get-playlist-tracks!
                    track-idx))

(defn find-playlist! [input]
  (find-items! #(search! "playlist" input)))

(defn view-playlist! [input]
  (view-item-tracks! #(search! "playlist" input)
                     get-playlist-tracks!))

(defn play-playlist! [input & [track-idx]]
  (play-item-track! #(search! "playlist" input)
                    get-playlist-tracks!
                    track-idx))

(defn find-album! [input]
  (find-items! #(search! "album" input)))

(defn view-album! [input]
  (view-item-tracks! #(search! "album" input)
                     get-album-tracks!))

(defn play-album! [input & [track-idx]]
  (play-item-track! #(search! "album" input)
                    get-album-tracks!
                    track-idx))

(defn run! [s]
  (fn []
    [(osa/tell! "Spotify" s)]))
