(ns jarvis.commands.basic
  (:require [clojure.string :as str]
            [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn set-volume! [[vol]]
  (try
    (osa/do! (str "set volume output volume " (Integer. vol)))
    (catch Exception e
      (speech/say! "volume should be an integer between 0 and 100."))))

(defn start-screensaver! [_]
  (osa/do! "System Events" "start current screen saver"))

(def commands
  [{:cmd ["print"]       :fn #(println (str/join " " %))}
   {:cmd ["say"]         :fn #(speech/say! (str/join  " " %))}
   {:cmd ["screensaver"] :fn start-screensaver!}
   {:cmd ["volume"]      :fn set-volume!}])
