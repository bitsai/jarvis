(ns jarvis.commands.basic
  (:require [clojure.java.shell :as shell]
            [jarvis.osascript :as osa]))

(defn print* [s]
  (println s)
  "ok")

(defn say [s]
  (shell/sh "say" s)
  "ok")

(defn set-volume [s]
  (if-let [v (try (Long. s) (catch Throwable t))]
    (osa/exec (format "set volume output volume %d" v))
    (format "volume should be an integer between 0 and 100")))

(defn start-screensaver [_]
  (osa/tell "System Events" "start current screen saver"))

(def commands
  [{:prefix ["print"]       :f print*}
   {:prefix ["say"]         :f say}
   {:prefix ["screensaver"] :f start-screensaver}
   {:prefix ["volume"]      :f set-volume}])
