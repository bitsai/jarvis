(ns jarvis.osascript
  (:require [clojure.java.shell :as shell]))

(defn run [s]
  (shell/sh "osascript" "-e" s)
  "ok")

(defn tell [app s]
  (run (format "tell application \"%s\" to %s" app s)))
