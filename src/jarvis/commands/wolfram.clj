(ns jarvis.commands.wolfram
  (:require [clj-http.client :as http]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn query [s]
  (let [query-params {:input s
                      :appid ""
                      :format "plaintext"
                      :podindex "1,2"}]
    (-> "http://api.wolframalpha.com/v2/query"
        (http/get {:query-params query-params :as :stream})
        (:body)
        (xml/parse)
        (zip/xml-zip))))

(defn ask [s]
  (let [z (query s)
        subPods (zip-xml/xml-> z :pod :subpod)]
    (if (seq subPods)
      (->> (for [sp subPods
                 :let [title ((zip-xml/attr :title) sp)
                       text (zip-xml/text sp)]]
             (str title ": " text))
           (str/join ", "))
      (throw (Exception. "No answers found.")))))

(def commands
  [{:prefix "alpha" :fn ask}])
