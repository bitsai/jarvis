(ns jarvis.commands.basic
  (:require [jarvis.util :as util]))

(defn set-volume [s]
  (if-let [v (try (Integer. s) (catch Throwable t))]
    (util/run (format "set volume output volume %d" v))
    (format "volume should be an integer between 0 and 100")))

(defn start-screensaver [_]
  (util/tell "System Events" "start current screen saver"))

(def commands
  [{:prefix "print"       :fun util/println}
   {:prefix "say"         :fun util/say}
   {:prefix "screensaver" :fun start-screensaver}
   {:prefix "volume"      :fun set-volume}])
