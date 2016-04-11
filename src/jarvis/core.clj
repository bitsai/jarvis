(ns jarvis.core
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as st]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.form :refer [form-to hidden-field]]
            [jarvis.commands.core :as cmd]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn render [input output]
  (html (form-to [:get "/"]
                 (hidden-field {:id :input} "input")
                 [:div
                  {:style "text-align:center"}
                  [:button
                   {:id :listen
                    :onclick "recognize()"
                    :style "font-size:60px; height:100px; width:900px"
                    :type :button}
                   "Listen"]])
        (when input
          (format "INPUT: %s" input))
        (repeat 2 [:br])
        (when output
          (str/replace output "\n" "<br>"))
        (javascript-tag (-> "jarvis.js" (io/resource) (slurp)))))

(defn handler [req]
  (let [input (-> req :params (get "input"))
        output (when (seq input)
                 (try
                   (cmd/run! input)
                   (catch Throwable t
                     (with-out-str (st/print-stack-trace t)))))]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (render input output)}))

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") cmd/run! println)
    (run-jetty (-> handler wrap-params wrap-reload) {:port 3000})))
