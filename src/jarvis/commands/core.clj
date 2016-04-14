(ns jarvis.commands.core
  (:refer-clojure :exclude [run!])
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.commands.wolfram :as wolfram]))

(def commands
  [;; basic
   {:cmd "print (.+)"        :fn basic/print!}
   {:cmd "say (.+)"          :fn basic/say!}
   {:cmd "set volume (\\d+)" :fn basic/set-volume!}
   {:cmd "start screensaver" :fn basic/start-screensaver!}
   ;; spotify
   {:cmd "my playlist"                     :fn spotify/my-playlists!}
   {:cmd "view playlist (.+)"              :fn spotify/view-playlist!}
   {:cmd "play playlist (.+) track (\\d+)" :fn spotify/play-playlist!}
   {:cmd "play playlist (.+)"              :fn spotify/play-playlist!}
   {:cmd "find album (.+)"                 :fn spotify/find-album!}
   {:cmd "view album (.+)"                 :fn spotify/view-album!}
   {:cmd "play album (.+) track (\\d+)"    :fn spotify/play-album!}
   {:cmd "play album (.+)"                 :fn spotify/play-album!}
   {:cmd "next track"                      :fn (spotify/run! "next track")}
   {:cmd "previous track"                  :fn (spotify/run! "previous track")}
   {:cmd "play music"                      :fn (spotify/run! "play")}
   {:cmd "stop music"                      :fn (spotify/run! "pause")}
   ;; weather announcement
   {:cmd "announce weather near (.+)" :fn weather/announce!}])

(defn- match [input command]
  (let [pattern (->> command :cmd (format "^%s$") re-pattern)
        matches (re-find pattern (str/trim input))]
    (cond
      (string? matches) [(:fn command) nil]
      (coll? matches)   [(:fn command) (->> matches rest (map str/trim))]
      :else             nil)))

(defn run! [input]
  (let [;; lower-case input before processing
        input (str/lower-case input)]
    (if (= input "help")
      (concat (map :cmd commands)
              ["DEFAULT: Wolfram Alpha query"])
      (if-let [[f args] (some (partial match input) commands)]
        (apply f args)
        (wolfram/ask! input)))))
