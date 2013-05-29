(ns jarvis.osa
  (:require [clojure.java.shell :as shell]))

(defn do! [cmd & [app]]
  (let [cmd-str (if app
                  (format "tell application \"%s\" to %s" app cmd)
                  cmd)]
    (shell/sh "osascript" "-e" cmd-str)))
