(ns jarvis.core
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as st]
            [clojure.string :as str]
            [hiccup.core :as h]
            [hiccup.element :as he]
            [hiccup.form :as hf]
            [jarvis.commands.core :as commands]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]))

(defn render [outputs]
  (h/html (hf/form-to [:post "/"]
                      (hf/text-field "input")
                      (hf/submit-button "Submit")
                      [:button
                       {:id "listen"
                        :onclick "recognize()"
                        :type "button"}
                       "Listen"])
          (interpose "<br><br>" outputs)
          (he/javascript-tag (-> "jarvis.js"
                                 (io/resource)
                                 (slurp)))))

(defn handler [req]
  (let [outputs (when-let [input (-> req :params (get "input"))]
                 (try
                   (commands/process input)
                   (catch Throwable t
                     [(with-out-str (st/print-stack-trace))])))]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (render outputs)}))

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") commands/process println)
    (run-jetty (wrap-params handler) {:port 3000})))
