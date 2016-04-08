(ns jarvis.html
  (require [clojure.java.io :as io]
           [hiccup.core :as h]
           [hiccup.element :as he]
           [hiccup.form :as hf]))

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
