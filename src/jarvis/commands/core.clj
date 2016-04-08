(ns jarvis.commands.core
  (:require [clojure.string :as str]
            [jarvis.commands.basic :as basic]
            [jarvis.commands.spotify :as spotify]
            [jarvis.commands.wolfram :as wolfram]))

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
