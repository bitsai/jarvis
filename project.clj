(defproject jarvis "0.1.0-SNAPSHOT"
  :dependencies [[cheshire "5.5.0"]
                 [clj-http "2.1.0"]
                 [compojure "1.5.0"]
                 [environ "1.0.2"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.8.0"]
                 [ring "1.4.0"]]
  :description "Voice-driven interface for stuff."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main jarvis.core
  :plugins [[lein-environ "1.0.2"]]
  :url "https://github.com/bitsai/jarvis")
