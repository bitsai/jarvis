(ns jarvis.core
  (:require [clojure.stacktrace :as st]
            [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]
            [jarvis.html :as html]
            [org.httpkit.server :as server]
            [ring.middleware.params :as params]))

(def commands
  (concat basic/commands
          spotify/commands))

(defn match? [tokens {:keys [prefix] :as cmd}]
  (->> tokens
       (map str/lower-case)
       (take (count prefix))
       (= prefix)))

(defn process [s]
  (let [tokens (str/split s #"\s+")]
    (if-let [{:keys [prefix f]} (->> commands
                                     (filter #(match? tokens %))
                                     (first))]
      (f (when-let [xs (->> tokens (drop (count prefix)) seq)]
           (str/join " " xs)))
      (wolfram/ask s))))

(defn handler [req]
  (let [input (-> req
                  (:params)
                  (get "input"))
        outputs (when input
                 (try
                   (process input)
                   (catch Throwable t
                     [(with-out-str (st/print-stack-trace))])))]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (html/render outputs)}))

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") process println)
    (do
      (server/run-server (params/wrap-params handler) {:port 8080})
      (println "ready!"))))
