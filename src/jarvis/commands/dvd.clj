(ns jarvis.commands.dvd
  (:require [jarvis.util :as util]))

(defn play [_]
  (util/tell "DVD player" "go to main menu")
  (util/tell "DVD player" "go to title menu")
  (Thread/sleep 16000)
  (util/tell "DVD player" "press enter key"))

(defn set-bool [setting]
  (fn [s]
    (if (#{"true" "false"} s)
      (util/tell "DVD player" (format "set %s to %s" setting s))
      (format "%s should be true or false" setting))))

(defn set-int [setting]
  (fn [s]
    (if-let [i (try (Integer. s) (catch Throwable t))]
      (util/tell "DVD player" (format "set %s to %d" setting i))
      (format "%s should be an integer" setting))))

(defn tell-dvd [s]
  (fn [_] (util/tell "DVD player" s)))

(def commands
  [{:prefix "dvd audio"      :fun (set-int "audio track")}
   {:prefix "dvd eject"      :fun (tell-dvd "eject dvd")}
   {:prefix "dvd fullscreen" :fun (set-bool "viewer full screen")}
   {:prefix "dvd play"       :fun play}
   {:prefix "dvd quit"       :fun (tell-dvd "quit")}
   {:prefix "dvd start"      :fun (tell-dvd "play dvd")}
   {:prefix "dvd stop"       :fun (tell-dvd "pause dvd")}
   {:prefix "dvd subtitle"   :fun (set-int "subtitle")}])
