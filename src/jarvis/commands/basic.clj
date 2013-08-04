(ns jarvis.commands.basic
  (:require [jarvis.osascript :as osa]
            [jarvis.util :as util]))

(defn set-volume [s]
  (let [msg "Volume should be an integer between 0 and 100."
        v (try
            (Integer. s)
            (catch Exception e
              (throw (Exception. msg))))]
    (osa/run (str "set volume output volume " v))))

(defn start-screensaver [_]
  (osa/tell "System Events" "start current screen saver"))

(def commands
  [{:prefix "print"       :fn println}
   {:prefix "say"         :fn util/say}
   {:prefix "screensaver" :fn start-screensaver}
   {:prefix "volume"      :fn set-volume}])
