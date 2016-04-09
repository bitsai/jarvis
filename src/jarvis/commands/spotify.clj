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

(defn- search! [category input]
  (-> (format "https://api.spotify.com/v1/search")
      (http/get {:query-params {:market (-> env :spotify-country (or "US"))
                                :q input
                                :type category}
                 :as :json})
      (:body)
      (get (keyword (str category "s")))
      (:items)))

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
    "playlist not found"))

(defn find! [category]
  (fn [input]
    (if-let [items (seq (search! category input))]
      (->> items
           (map :name)
           (str/join "\n"))
      (format "no %ss found" category))))

(defn play! [category]
  (fn [input]
    (if-let [item (first (search! category input))]
      (osa/tell! "Spotify" (format "play track \"%s\"" (:uri item)))
      (format "%s not found" category))))

(defn tell! [s]
  (fn [_] (osa/tell! "Spotify" s)))
