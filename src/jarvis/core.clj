(ns jarvis.core
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as st]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.form :refer [form-to hidden-field]]
            [jarvis.commands.core :as cmd]
            [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]))

(defn render [input output]
  (html (form-to [:get "/"]
                 (hidden-field {:id :input} "input")
                 [:button
                  {:id :listen
                   :onclick "recognize()"
                   :type :button}
                  "Listen"])
        (when input
          (format "INPUT: %s" input))
        [:br]
        [:br]
        output
        (javascript-tag (-> "jarvis.js" (io/resource) (slurp)))))

(defn handler [req]
  (let [input (-> req :params (get "input"))
        output (when (seq input)
                 (try
                   (cmd/process input)
                   (catch Throwable t
                     [(with-out-str (st/print-stack-trace t))])))]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (render input output)}))

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") cmd/process println)
    (j/run-jetty (p/wrap-params handler) {:port 3000})))
