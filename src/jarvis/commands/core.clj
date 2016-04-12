(ns jarvis.commands.core
  (:refer-clojure :exclude [run!])
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.weather :as weather]
            [jarvis.commands.wolfram :as wolfram]))

(def commands
  [;; basic
   {:cmd "print"             :fn basic/print!}
   {:cmd "say"               :fn basic/say!}
   {:cmd "start screensaver" :fn basic/start-screensaver!}
   {:cmd "set volume"        :fn basic/set-volume!}
   ;; spotify
   {:cmd "find playlist"  :fn spotify/find-playlist!}
   {:cmd "play playlist"  :fn spotify/play-playlist!}
   {:cmd "find album"     :fn (spotify/find! "album")}
   {:cmd "play album"     :fn (spotify/play! "album")}
   {:cmd "find track"     :fn (spotify/find! "track")}
   {:cmd "play track"     :fn (spotify/play! "track")}
   {:cmd "next track"     :fn (spotify/tell! "next track")}
   {:cmd "previous track" :fn (spotify/tell! "previous track")}
   {:cmd "play music"     :fn (spotify/tell! "play")}
   {:cmd "stop music"     :fn (spotify/tell! "pause")}
   ;; weather
   {:cmd "weather near" :fn weather/announce!}])

(defn- match [input command]
  (let [pattern (->> command :cmd (format "^%s(.*)$") re-pattern)]
    (when-let [[_ args] (re-find pattern (str/trim input))]
      [(:fn command) (str/trim args)])))

(defn run! [input]
  (let [;; lower-case input before processing
        input (str/lower-case input)]
    (if-let [[f args] (some (partial match input) commands)]
      (f args)
      (wolfram/ask! input))))
