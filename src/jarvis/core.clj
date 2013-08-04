(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.util :as util]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]))

(def commands
  (concat basic/commands
          dvd/commands
          spotify/commands))

(defn exact-match? [s prefix]
  (let [n (count prefix)]
    (and (= prefix (->> s (take n) (apply str)))
         (or (= n (count s))
             (= \space (nth s n))))))

(defn find-command [s]
  (->> commands
       (filter #(->> % :prefix (exact-match? s)))
       (first)))

(defn execute [s]
  (try
    (let [cmd (find-command s)]
      (if-not cmd
        "Command not found."
        (->> s
             (drop (count (:prefix cmd)))
             (apply str)
             (str/trim)
             ((:fn cmd)))))
    (catch Exception e
      (.getMessage e))))

(defn handler [req]
  (-> req
      (:body)
      (slurp)
      (execute)
      (resp/response)))

(defn -main [& args]
  (let [s (str/join " " args)]
    (if (pos? (count s))
      (when-let [r (execute s)]
        (util/say r))
      (jetty/run-jetty handler {:port 8080}))))
