(ns jarvis.util
  (:refer-clojure :exclude [println])
  (:require [clojure.java.shell :as shell]))

(defn println [s]
  (clojure.core/println s)
  "ok")

(defn run [s]
  (shell/sh "osascript" "-e" s)
  "ok")

(defn say [s]
  (shell/sh "say" s)
  "ok")

(defn tell [app s]
  (run (format "tell application \"%s\" to %s" app s)))
