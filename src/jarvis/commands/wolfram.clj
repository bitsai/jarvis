(ns jarvis.commands.wolfram
  (:require [clj-http.client :as http]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zf-xml]
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

;; use this because zf-xml/text snarfs newline characters
(defn text [loc]
  (apply str (zf-xml/xml-> loc zf/descendants zip/node string?)))

(defn clean [s]
  (-> s
      (str/replace "|" ",")
      (str/replace "\n" ",\n")
      (str/replace "°F" "° Fahrenheit")))

(defn ask [s]
  (let [z (query s)
        subpods (zf-xml/xml-> z :pod :subpod)]
    (if (seq subpods)
      (->> subpods
           (mapcat (juxt (zf-xml/attr :title) (comp clean text)))
           (str/join ",\n"))
      (throw (Exception. "No answers found.")))))

(def commands
  [{:prefix "alpha" :fn ask}])
