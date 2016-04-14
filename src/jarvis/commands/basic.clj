(ns jarvis.commands.basic
  (:require [clojure.java.shell :as shell]
            [jarvis.osascript :as osa]))

(defn print! [input]
  (println input)
  [input])

(defn say! [input]
  (shell/sh "say" input)
  ["success"])

(defn set-volume! [input]
  [(osa/run! (format "set volume output volume %d" (Long. input)))])

(defn start-screensaver! []
  [(osa/tell! "System Events" "start current screen saver")])
