(defproject jarvis "0.1.0-SNAPSHOT"
  :aliases {"server" ["run" "-m" "jarvis.server/start!"]}
  :dependencies [[clj-http "0.7.2"]
                 [clj-time "0.5.1"]
                 [com.googlecode.soundlibs/jlayer "1.0.1-1"]
                 [org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]]
  :description "An imitation of Jarvis from Iron Man"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main jarvis.cli
  :url "https://github.com/bitsai/jarvis")
