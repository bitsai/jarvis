(ns jarvis.speech
  (:import [javazoom.jl.player Player])
  (:require [clj-http.client :as http]))

(defn say! [text]
  (-> "http://translate.google.com/translate_tts"
      (http/get {:query-params {:tl "en" :q text} :as :stream})
      (:body)
      (Player.)
      (.play)))
