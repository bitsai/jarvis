(ns jarvis.commands.util
  (:require [clojure.java.shell :as sh]))

(defn osa [app cmd]
  (let [cmd-str (format "tell application \"%s\" to %s" app cmd)]
    (sh/sh "osascript" "-e" cmd-str)))
