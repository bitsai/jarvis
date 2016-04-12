(ns jarvis.commands.basic
  (:require [clojure.java.shell :as shell]
            [jarvis.osascript :as osa]))

(defn print! [input]
  (println input)
  input)

(defn say! [input]
  (shell/sh "say" input)
  "success")

(defn set-volume! [input]
  (->> (Long. input)
       (format "set volume output volume %d")
       (osa/run!)))

(defn start-screensaver! []
  (osa/tell! "System Events" "start current screen saver"))
