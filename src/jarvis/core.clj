(ns jarvis.core
  (:require [clojure.stacktrace :as st]
            [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]
            [org.httpkit.server :as server]))

(def all-commands
  (concat basic/commands
          spotify/commands))

(defn match? [words {:keys [prefix] :as cmd}]
  (->> words
       (map str/lower-case words)
       (take (count prefix))
       (= prefix)))

(defn process [s]
  (let [words (str/split s #"\s+")]
    (try
      (if-let [{:keys [prefix f]} (->> all-commands
                                       (filter #(match? words %))
                                       (first))]
        (f (->> words (drop (count prefix)) (str/join " ")))
        (wolfram/ask s))
      (catch Throwable t
        (with-out-str (st/print-stack-trace t))))))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/html;charset=UTF-8"}
   :body (-> req
             (:body)
             (slurp)
             (process)
             (str "\n"))})

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") process println)
    (do
      (server/run-server handler {:port 8080})
      (println "ready!"))))
