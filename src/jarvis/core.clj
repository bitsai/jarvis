(ns jarvis.core
  (:require [clojure.string :as str]
            [jarvis.commands.weather :as weather]
            [jarvis.speech :as speech]))

(def commands
  [{:prefix "print"   :fn println}
   {:prefix "say"     :fn speech/say!}
   {:prefix "weather" :fn weather/announce!}])

(defn matches? [prefix s]
  (re-find (re-pattern (str "^" prefix " ")) s))

(defn process! [s]
  (if-let [cmd (->> commands
                    (filter #(-> % :prefix (matches? s)))
                    first)]
    ((:fn cmd) (subs s (-> cmd :prefix count inc)))
    (speech/say! "I don't know that command.")))
