(ns jarvis.commands.wolfram
  (:require [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zf-xml]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [org.httpkit.client :as http]))

(def app-id "")

(defn query [s]
  (let [query-params {:appid app-id :format "plaintext" :input s}]
    (-> "http://api.wolframalpha.com/v2/query"
        (http/get {:query-params query-params :as :stream})
        (deref)
        (:body))))

;; use this because zf-xml/text snarfs newline characters
(defn ->text [z]
  (apply str (zf-xml/xml-> z zf/descendants zip/node string?)))

(defn no-leading-newline [s]
  (str/replace s #"^\n" ""))

(defn parse-subpod [subpod]
  (let [title (zf-xml/attr subpod :title)
        text (-> subpod ->text no-leading-newline)]
    (when (seq text)
      (if (seq title)
        (str/join "\n" [title text])
        text))))

(defn parse-pod [pod]
  (let [title (zf-xml/attr pod :title)
        texts (->> (zf-xml/xml-> pod :subpod)
                   (keep parse-subpod)
                   (str/join "\n"))]
    (when (seq texts)
      (str/join "\n" [title texts]))))

(defn parse-body [body]
  (let [z (-> body xml/parse zip/xml-zip)
        pods (zf-xml/xml-> z :pod)]
    (if (seq pods)
      (->> pods
           (keep parse-pod)
           (str/join "\n\n"))
      "no results found")))

(defn process [s]
  (-> s query parse-body))
