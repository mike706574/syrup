(ns syrup.core.alpha
  (:require [pancake.core :as pancake]
            [tailor.transform :as transform]
            [tailor.validation :as validation]))

(defn field-specs
  [format]
  (->> (:fields format)
       (filter :spec)
       (map #(vector (:id %) (:spec %)))
       (into {})))

(defn ingest [format collect lines]
  (let [parser (pancake/parse format)
        validator (if-let [spec (:spec format)]
                    (validation/validate spec)
                    (let [specs (field-specs format)]
                      (when-not (empty? specs)
                        (validation/validate-with-specs specs))))
        xf (if validator
             (comp parser validator)
             parser)]
    (collect xf lines)))

(defn validate [format lines] (ingest format transform/summarize lines))
(defn collect [format lines] (ingest format transform/collect lines))
