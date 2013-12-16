(ns jarvis.core
  (:require [clojure.stacktrace :as st]
            [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.dvd :as dvd]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]))

(def all-commands
  (concat basic/commands
          dvd/commands
          spotify/commands))

(defn match? [s {:keys [prefix] :as command}]
  (let [prefix-len (count prefix)]
    ;; besides having the prefix, s should also either have the same length
    ;; as prefix or have a space after prefix's end to avoid false matches
    ;; like string "println ..." with prefix "print".
    (and (->> s (take prefix-len) (apply str) (= prefix))
         (or (= prefix-len (count s))
             (= \space (nth s prefix-len))))))

(defn process [s]
  (let [s (str/lower-case s)]
    (try
      (if-let [{:keys [fun prefix]} (->> all-commands
                                         (filter #(match? s %))
                                         (first))]
        (fun (-> s (subs (count prefix)) str/trim))
        (wolfram/process s))
      (catch Throwable t
        (with-out-str (st/print-stack-trace t))))))

(defn handler [req]
  (-> req
      (:body)
      (slurp)
      (process)
      (str "\n")
      (resp/response)
      (resp/charset "UTF-8")))

(defn -main [& args]
  (let [s (str/join " " args)]
    (if (seq s)
      (println (process s))
      (jetty/run-jetty handler {:port 8080}))))
