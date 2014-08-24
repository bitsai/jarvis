(ns jarvis.commands.wolfram
  (:require [clojure.data.zip.xml :as zf-xml]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [environ.core :as e]
            [org.httpkit.client :as http]))

(defn query [app-id s]
  (let [params {:appid app-id :format "image" :input s}]
    (-> "http://api.wolframalpha.com/v2/query"
        (http/get {:query-params params :as :stream})
        (deref)
        (:body))))

(defn parse-errors [zipper]
  (->> (zf-xml/xml-> zipper :error :msg)
       (map zf-xml/text)))

(defn parse-results [zipper]
  (->> (zf-xml/xml-> zipper :pod)
       (map (fn [pod]
              (let [title (zf-xml/attr pod :title)
                    img-src (-> pod
                                (zf-xml/xml-> :subpod :img)
                                (first)
                                (zf-xml/attr :src))]
                (format "%s<br><img src=\"%s\">" title img-src))))))

(defn parse-xml [xml]
  (let [z (-> xml xml/parse zip/xml-zip)
        errors (parse-errors z)
        results (parse-results z)]
    (cond
     (seq errors)  errors
     (seq results) results
     :else         ["no results found"])))

(defn ask [s]
  (->> s (query (e/env :app-id)) parse-xml (str/join "<br><br>")))
