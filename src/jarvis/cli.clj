(ns jarvis.cli
  (:require [clojure.string :as str]
            [jarvis.core :as jarvis]))

(defn -main [& args]
  (jarvis/process! (str/join " " args)))
