(ns jarvis.commands.wolfram
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [environ.core :refer [env]]))

(defn- query! [input]
  (-> "http://api.wolframalpha.com/v2/query"
      (http/get {:query-params {:appid (:wolfram-alpha-app-id env)
                                :format "plaintext"
                                :input input}
                 :as :stream})
      (:body)
      (xml/parse)))

(defn- parse-subpod [subpod]
  (when-let [plaintext (some->> subpod
                                (:content)
                                (filter #(-> % :tag (= :plaintext)))
                                (first)
                                (:content)
                                (first)
                                (str/trim))]
    (let [title (-> subpod :attrs :title)]
      (if (seq title)
        (format "%s\n%s" title plaintext)
        plaintext))))

(defn- parse-pod [pod]
  (when-let [parsed-subpods (->> pod
                                 (:content)
                                 (filter #(-> % :tag (= :subpod)))
                                 (keep parse-subpod)
                                 (seq))]
    (->> parsed-subpods
         (concat [(-> pod :attrs :title)])
         (str/join "\n"))))

(defn- parse-query-result [query-result]
  (when-let [parsed-pods (->> query-result
                              (:content)
                              (filter #(-> % :tag (= :pod)))
                              (keep parse-pod)
                              (seq))]
    (str/join "\n\n" parsed-pods)))

(defn ask! [input]
  (if-let [parsed-query-result (-> input query! parse-query-result)]
    parsed-query-result
    "no results found"))
