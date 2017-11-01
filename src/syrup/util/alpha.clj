(ns syrup.util.alpha
  (:require [clojure.pprint :as pprint]
            [clojure.spec.alpha :as s]))

(defn pred-name [pred]
  (cond (string? pred) pred
        (symbol? pred) (name pred)
        (and (sequential? pred)
             (= (first pred) 'clojure.spec.alpha/conformer)) (pred-name (second pred))
        (sequential? pred) (pr-str (s/abbrev pred))
        :else (str pred)))

(defn boxed [s]
  (let [enclosed (str "| " s " |")
        dashes (str (apply str (repeat (count enclosed) \-)))]
    (str dashes \newline
         enclosed \newline
         dashes \newline)))

(defn pretty [form]
  (with-out-str (pprint/pprint form)))
