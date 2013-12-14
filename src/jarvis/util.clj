(ns jarvis.util
  (:refer-clojure :exclude [println])
  (:require [clojure.java.shell :as shell]))

(defn has-prefix? [s prefix]
  (let [prefix-len (count prefix)]
    ;; besides having the prefix, s should also either have the same length as
    ;; prefix or have a space after prefix's end to avoid false matches like
    ;; "println ..." with prefix "print".
    (and (->> s (take prefix-len) (apply str) (= prefix))
         (or (= prefix-len (count s))
             (= \space (nth s prefix-len))))))

(defn find-command [s commands]
  (->> commands
       (filter #(has-prefix? s (:prefix %)))
       (first)))

(defn println [s]
  (clojure.core/println s)
  "ok")

(defn say [s]
  (shell/sh "say" s)
  "ok")
