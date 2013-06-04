(ns jarvis.osa
  (:require [clojure.java.shell :as shell]))

(defn do!
  ([cmd]
     (shell/sh "osascript" "-e" cmd))
  ([app cmd]
     (do! (format "tell application \"%s\" to %s" app cmd))))
