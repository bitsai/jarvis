(ns jarvis.commands.spotify
  (:refer-clojure :exclude [run!])
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [jarvis.osascript :as osa])
  (:import [javax.xml.bind DatatypeConverter]))

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

(defn- get-playlists! []
  (-> (format "https://api.spotify.com/v1/users/%s/playlists"
              (:spotify-user-id env))
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

(defn show-my-playlists! []
  (if-let [items (seq (get-playlists!))]
    (map :name items)
    ["no playlists found"]))

(defn play-my-playlist! [input]
  (if-let [item (->> (get-playlists!)
                     (filter #(-> % :name str/lower-case (= input)))
                     (first))]
    [(osa/tell! "Spotify" (format "play track \"%s\"" (:uri item)))]
    ["playlist not found"]))

(defn find! [category]
  (fn [input]
    (if-let [items (seq (search! category input))]
      (map :name items)
      [(format "no %ss found" category)])))

(defn play! [category]
  (fn [input]
    (if-let [item (first (search! category input))]
      [(osa/tell! "Spotify" (format "play track \"%s\"" (:uri item)))]
      [(format "%s not found" category)])))

(defn run! [s]
  (fn []
    [(osa/tell! "Spotify" s)]))
