(ns jarvis.osascript
  (:require [clojure.java.shell :as shell]))

(defn exec [s]
  (shell/sh "osascript" "-e" s)
  "success")

(defn tell [app s]
  (exec (format "tell application \"%s\" to %s" app s)))
