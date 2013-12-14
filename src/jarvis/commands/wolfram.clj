(ns jarvis.commands.wolfram
  (:require [clj-http.client :as http]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zf-xml]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn query [s]
  (let [query-params {:appid "" :input s :podindex "1,2"}]
    (-> "http://api.wolframalpha.com/v2/query"
        (http/get {:query-params query-params :as :stream})
        (:body))))

(defn ->html [s]
  (-> s
      (str/replace "\n" "<br>")
      (str/replace "°" "")))

;; use this because zf-xml/text snarfs newline characters
(defn ->text [loc]
  (apply str (zf-xml/xml-> loc zf/descendants zip/node string?)))

(defn ->data [subpod]
  (let [title (zf-xml/attr subpod :title)
        text (-> subpod ->text ->html)]
    (if (seq title)
      (format "<b>%s</b><br>%s" title text)
      text)))

(defn parse [body]
  (let [z (-> body xml/parse zip/xml-zip)
        subpods (zf-xml/xml-> z :pod :subpod)]
    (if (seq subpods)
      (->> subpods
           (map ->data)
           (str/join "<br>"))
      (throw (Exception. "no results found")))))

(defn process [s]
  (-> s query parse))
