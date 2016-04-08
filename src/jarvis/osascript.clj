(ns jarvis.osascript
  (:refer-clojure :exclude [run!])
  (:require [clojure.java.shell :as shell]))

(defn run! [input]
  (shell/sh "osascript" "-e" input)
  "success")

(defn tell! [app input]
  (run! (format "tell application \"%s\" to %s" app input)))
