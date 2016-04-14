(ns jarvis.core
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as st]
            [clojure.string :as str]
            [compojure.core :refer [defroutes GET]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.form :refer [form-to hidden-field]]
            [jarvis.commands.core :as cmd]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]))

(defn- handle-input! [input]
  (when (seq input)
    (try
      (cmd/run! input)
      (catch Throwable t
        (with-out-str (st/print-stack-trace t))))))

(defn- render [input output]
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

(defroutes app
  (GET "/" [input]
       (let [output (handle-input! input)]
         {:status 200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body (render input output)})))

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") handle-input! println)
    (run-jetty (wrap-params app) {:port 3000})))
