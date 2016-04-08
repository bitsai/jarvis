(ns jarvis.commands.wolfram
  (:require [clj-http.client :as http]
            [clojure.data.zip.xml :as zf-xml]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [environ.core :as e]))

(defn query [s & [app-id]]
  (let [app-id (or app-id (-> e/env :wolfram-alpha :app-id))]
    (-> "http://api.wolframalpha.com/v2/query"
        (http/get {:query-params {:appid app-id :format "image" :input s}
                   :as :stream})
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
  (->> s query parse-xml))
