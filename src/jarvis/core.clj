(ns jarvis.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.stacktrace :as st]
            [clojure.string :as str]
            [compojure.core :refer [defroutes GET POST]]
            [environ.core :refer [env]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.form :refer [form-to hidden-field]]
            [jarvis.commands.core :as cmd]
            [jarvis.facebook :as fb]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]))

(defn- handle-input! [input]
  (when (seq input)
    (try
      (cmd/run! input)
      (catch Throwable t
        [(with-out-str (st/print-stack-trace t))]))))

(defn- render [input outputs]
  (html (form-to [:get "/"]
                 (hidden-field {:id :input} "input")
                 [:div
                  {:style "text-align:center"}
                  [:div
                   {:class "fb-send-to-messenger"
                    :messenger_app_id (:facebook-app-id env)
                    :page_id (:facebook-page-id env)
                    :data-ref ""
                    :color "blue"
                    :size "xlarge"}]
                  [:button
                   {:id :listen
                    :onclick "recognize()"
                    :style "font-size:60px; height:100px; width:600px"
                    :type :button}
                   "Listen"]])
        (when input
          (format "INPUT: %s" input))
        (repeat 2 [:br])
        (->> outputs
             (map #(str/replace % "\n" "<br>"))
             (interpose (repeat 2 [:br])))
        (javascript-tag (-> "jarvis.js" (io/resource) (slurp)))
        (javascript-tag (-> "facebook.js"
                            (io/resource)
                            (slurp)
                            (str/replace "APP_ID" (:facebook-app-id env))))))

(defroutes app
  (GET "/" [input]
       (let [outputs (handle-input! input)]
         {:status 200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body (render input outputs)}))
  (GET "/facebook-webhook" request
       (-> request :params (get "hub.challenge")))
  (POST "/facebook-webhook" request
        (doseq [event (-> request
                          (:body)
                          (slurp)
                          (json/parse-string true)
                          (:entry)
                          (first)
                          (:messaging))
                :let [sender-id (-> event :sender :id)]]
          (when-let [data-ref (-> event :optin :ref)]
            (fb/send-message! sender-id "Hello!"))
          (when-let [input (-> event :message :text)]
            (doseq [output (handle-input! input)]
              (fb/send-message! sender-id output))))
        {:status 200}))

(defn -main [& args]
  (if (seq args)
    (->> args (str/join " ") handle-input! (str/join "\n\n") println)
    (run-jetty (wrap-params app) {:port 3000})))
