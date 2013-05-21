(ns jarvis.cli
  (:require [jarvis.core :as jarvis]))

(defn -main [& args]
  (jarvis/process! args))
