(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]
            [jarvis.util :as util]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]))

(def all-commands
  (concat basic/commands
          dvd/commands
          spotify/commands))

(defn process [s]
  (try
    (if-let [cmd (util/find-command s all-commands)]
      (->> s
           (drop (count (:prefix cmd)))
           (apply str)
           (str/trim)
           ((:fn cmd)))
      (wolfram/process s))
    (catch Throwable e
      (.getMessage e))))

(defn handler [req]
  (-> req
      (:body)
      (slurp)
      (process)
      (str "\n")
      (resp/response)))

(defn -main [& args]
  (let [s (->> args (str/join " ") str/lower-case)]
    (if (seq s)
      (println (process s))
      (jetty/run-jetty handler {:port 8080}))))
