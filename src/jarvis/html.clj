(ns jarvis.html
  (require [clojure.java.io :as io]
           [hiccup.core :as h]
           [hiccup.element :as he]
           [hiccup.form :as hf]))

(defn render [outputs]
  (h/html (hf/form-to [:post "/"]
                      (hf/text-field "input")
                      [:button
                       {:id "listen"
                        :onclick "recognize()"
                        :type "button"}
                       "Listen"]
                      (hf/submit-button "Submit"))
          (interpose "<br><br>" outputs)
          (he/javascript-tag (-> "jarvis.js"
                                 (io/resource)
                                 (slurp)))))
