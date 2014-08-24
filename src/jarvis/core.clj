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
    (println (process (str/join " " args)))
    (do
      (server/run-server handler {:port 8080})
      (println "ready!"))))
