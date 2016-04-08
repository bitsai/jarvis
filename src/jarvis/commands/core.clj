(ns jarvis.commands.core
  (:refer-clojure :exclude [run!])
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]))

(def commands
  [;; basic
   {:cmd "print"             :fn basic/print!}
   {:cmd "say"               :fn basic/say!}
   {:cmd "start screensaver" :fn basic/start-screensaver!}
   {:cmd "set volume"        :fn basic/set-volume!}
   ;; spotify
   {:cmd "find playlist" :fn spotify/find-playlists!}
   {:cmd "play playlist" :fn spotify/play-playlist!}
   {:cmd "find album"    :fn spotify/find-albums!}
   {:cmd "play album"    :fn spotify/play-album!}
   {:cmd "find track"    :fn spotify/find-tracks!}
   {:cmd "play track"    :fn spotify/play-track!}
   {:cmd "next track"    :fn (spotify/tell! "next track")}
   {:cmd "last track"    :fn (spotify/tell! "previous track")}
   {:cmd "play"          :fn (spotify/tell! "play")}
   {:cmd "pause"         :fn (spotify/tell! "pause")}])

(defn- match [input command]
  (let [pattern (->> command :cmd (format "^%s(.*)$") re-pattern)]
    (when-let [[_ args] (re-find pattern (-> input str/lower-case str/trim))]
      [(:fn command) (str/trim args)])))

(defn run! [input]
  (if-let [[f args] (some (partial match input) commands)]
    (f args)
    "TODO: Google search"))
