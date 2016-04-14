(ns jarvis.facebook
  (:require [clj-http.client :as http]
            [environ.core :refer [env]]))

(defn- trim [s]
  (if (-> s count (> 320))
    (format "%s..." (subs s 0 317))
    s))

(defn send-message! [recipient-id message]
  (-> (format "https://graph.facebook.com/v2.6/me/messages?access_token=%s"
              (-> env :facebook-page-access-token))
      (http/post {:form-params {:recipient {:id recipient-id}
                                :message {:text (trim message)}}})))
