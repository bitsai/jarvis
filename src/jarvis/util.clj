(ns jarvis.util
  (:require [clojure.java.shell :as shell]))

(defn say [s]
  (shell/sh "say" s)
  nil)
