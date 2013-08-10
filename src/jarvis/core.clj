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
          spotify/commands
          wolfram/commands))

(defn has-prefix? [s prefix]
  (let [prefix-len (count prefix)]
    ;; besides having the prefix, s should also either have the
    ;; same length as prefix or have a space after prefix's end
    ;; to avoid false matches like "println ..." with prefix "print"
    (and (->> s (take prefix-len) (apply str) (= prefix))
         (or (= prefix-len (count s))
             (= \space (nth s prefix-len))))))

(defn find-command [s commands]
  (->> commands
       (filter #(has-prefix? s (:prefix %)))
       (first)))

(defn execute [s]
  (try
    (let [cmd (find-command s all-commands)]
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
      (str "\n")
      (resp/response)))

(defn -main [& args]
  (let [s (str/join " " args)]
    (if (pos? (count s))
      (when-let [r (execute s)]
        (util/say r))
      (jetty/run-jetty handler {:port 8080}))))
