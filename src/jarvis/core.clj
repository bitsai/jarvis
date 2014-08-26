(ns jarvis.core
  (:require [clojure.stacktrace :as st]
            [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]
            [org.httpkit.server :as server]
            [ring.middleware.params :as params]))

(def commands
  (concat basic/commands
          spotify/commands))

(defn match? [words {:keys [prefix] :as cmd}]
  (->> words
       (map str/lower-case)
       (take (count prefix))
       (= prefix)))

(defn process [s]
  (let [words (str/split s #"\s+")]
    (if-let [{:keys [prefix f]} (->> commands
                                     (filter #(match? words %))
                                     (first))]
      (f (when-let [xs (->> words (drop (count prefix)) seq)]
           (str/join " " xs)))
      (wolfram/ask s))))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/html;charset=UTF-8"}
   :body (-> (try
               (-> req
                   (:params)
                   (get "input")
                   (process))
               (catch Throwable t
                 (with-out-str (st/print-stack-trace t))))
             (str "\n"))})

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") process println)
    (do
      (server/run-server (params/wrap-params handler) {:port 8080})
      (println "ready!"))))
