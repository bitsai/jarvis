(ns jarvis.commands.basic
  (:require [clojure.java.shell :as shell]
            [jarvis.osascript :as osa]))

(defn print! [s]
  (println s)
  s)

(defn say! [s]
  (shell/sh "say" s)
  "success")

(defn set-volume! [s]
  (if-let [v (try (Long. s) (catch Throwable t))]
    (osa/run! (format "set volume output volume %d" v))
    (format "volume should be an integer between 0 and 100")))

(defn start-screensaver! [_]
  (osa/tell! "System Events" "start current screen saver"))
