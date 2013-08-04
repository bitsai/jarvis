(ns jarvis.commands.dvd
  (:require [jarvis.osascript :as osa]))

(defn play [_]
  (osa/tell "DVD player" "go to main menu")
  (osa/tell "DVD player" "go to title menu")
  (Thread/sleep 16000)
  (osa/tell "DVD player" "press enter key"))

(defn set-bool [setting]
  (fn [s]
    (if-not (#{"true" "false"} s)
      (throw (Exception. (str setting " should be true or false.")))
      (osa/tell "DVD player" (format "set %s to %s" setting s)))))

(defn set-int [setting]
  (fn [s]
    (let [msg (str setting " should be an integer.")
          i (try
              (Integer. s)
              (catch Exception e
                (throw (Exception. msg))))]
      (osa/tell "DVD player" (format "set %s to %d" setting i)))))

(defn tell-dvd [s]
  (fn [_] (osa/tell "DVD player" s)))

(def commands
  [{:prefix "dvd audio"      :fn (set-int "audio track")}
   {:prefix "dvd eject"      :fn (tell-dvd "eject dvd")}
   {:prefix "dvd fullscreen" :fn (set-bool "viewer full screen")}
   {:prefix "dvd play"       :fn play}
   {:prefix "dvd quit"       :fn (tell-dvd "quit")}
   {:prefix "dvd start"      :fn (tell-dvd "play dvd")}
   {:prefix "dvd stop"       :fn (tell-dvd "pause dvd")}
   {:prefix "dvd subtitle"   :fn (set-int "subtitle")}])
