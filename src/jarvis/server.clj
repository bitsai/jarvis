(ns jarvis.server
  (:require [jarvis.core :as jarvis]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]))

(defn handler [req]
  (-> req
      (:body)
      (slurp)
      (jarvis/process!))
  (resp/response nil))

(defn start! []
  (jetty/run-jetty handler {:port 8080}))
