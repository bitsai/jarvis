(ns jarvis.html
  (require [hiccup.core :as h]
           [hiccup.form :as hf]))

(defn render [outputs]
  (h/html (hf/form-to [:post "/"]
                      (hf/text-field "input")
                      (hf/submit-button "Submit"))
          (interpose "<br><br>" outputs)))
