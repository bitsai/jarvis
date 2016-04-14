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
   ;;{:cmd "say (.+)"          :fn basic/say!}
   ;;{:cmd "set volume (\\d+)" :fn basic/set-volume!}
   ;;{:cmd "start screensaver" :fn basic/start-screensaver!}
   ;; spotify
   {:cmd "show my playlist"      :fn spotify/show-my-playlists!}
   ;;{:cmd "play my playlist (.+)" :fn spotify/play-my-playlist!}
   {:cmd "find album (.+)"       :fn (spotify/find! "album")}
   ;;{:cmd "play album (.+)"       :fn (spotify/play! "album")}
   {:cmd "find artist (.+)"      :fn (spotify/find! "artist")}
   ;;{:cmd "play artist (.+)"      :fn (spotify/play! "artist")}
   {:cmd "find playlist (.+)"    :fn (spotify/find! "playlist")}
   ;;{:cmd "play playlist (.+)"    :fn (spotify/play! "playlist")}
   {:cmd "find track (.+)"       :fn (spotify/find! "track")}
   ;;{:cmd "play track (.+)"       :fn (spotify/play! "track")}
   ;;{:cmd "next track"            :fn (spotify/run! "next track")}
   ;;{:cmd "previous track"        :fn (spotify/run! "previous track")}
   ;;{:cmd "play music"            :fn (spotify/run! "play")}
   ;;{:cmd "stop music"            :fn (spotify/run! "pause")}
   ;; weather
   ;;{:cmd "announce weather near (.+)" :fn weather/announce!}
   ])

(defn- match [input command]
  (let [pattern (->> command :cmd (format "^%s$") re-pattern)
        matches (re-find pattern (str/trim input))]
    (cond
      (string? matches) [(:fn command) nil]
      (coll? matches)   [(:fn command) [(-> matches second str/trim)]]
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
