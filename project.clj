(defproject jarvis "0.1.0-SNAPSHOT"
  :dependencies [[environ "1.0.0"]
                 [http-kit "2.1.18"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/data.zip "0.1.1"]
                 [ring/ring-core "1.3.0"]]
  :description "Voice-driven interface for stuff."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main jarvis.core
  :url "https://github.com/bitsai/jarvis")
