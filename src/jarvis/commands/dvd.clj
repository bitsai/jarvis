(ns jarvis.commands.dvd
  (:require [jarvis.osa :as osa]
            [jarvis.speech :as speech]))

(defn do! [cmd]
  (fn [_] (osa/do! "DVD player" cmd)))

(defn play! [_]
  (osa/do! "DVD player" "go to main menu")
  (osa/do! "DVD player" "go to title menu")
  (Thread/sleep 16000)
  (osa/do! "DVD player" "press enter key"))

(defn set-fullscreen! [[setting]]
  (case setting
    "on"  (osa/do! "DVD player" "set viewer full screen to true")
    "off" (osa/do! "DVD player" "set viewer full screen to false")
    (speech/say! "please say on or off.")))

(defn set-numeric! [setting]
  (fn [[w]]
    (try
      (osa/do! "DVD player" (str "set " setting " to " (Integer. w)))
      (catch Exception e
        (speech/say! (str setting " should be an integer."))))))

(def commands
  [{:cmd ["dvd" "audio"]      :fn (set-numeric! "audio track")}
   {:cmd ["dvd" "eject"]      :fn (do! "eject dvd")}
   {:cmd ["dvd" "fullscreen"] :fn set-fullscreen!}
   {:cmd ["dvd" "play"]       :fn play!}
   {:cmd ["dvd" "quit"]       :fn (do! "quit")}
   {:cmd ["dvd" "resume"]     :fn (do! "play dvd")}
   {:cmd ["dvd" "stop"]       :fn (do! "pause dvd")}
   {:cmd ["dvd" "subtitle"]   :fn (set-numeric! "subtitle")}])
