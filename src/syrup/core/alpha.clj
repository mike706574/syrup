(ns syrup.core.alpha
  (:require [pancake.core :as pancake]
            [pancake.format :as format]
            [tailor.transform :as transform]
            [tailor.validation :as validation]))

(defn ingest [format collect lines]
  (let [lines (if-let [skip (:skip format)]
                (drop skip lines)
                lines)
        record-spec (:spec format)
        field-specs (format/value-specs format)
        parser (pancake/parse format)
        validator (validation/conform record-spec field-specs)
        xf (if validator
             (comp parser validator)
             parser)]
    (collect xf lines)))

(defn validate [format lines] (ingest format transform/summarize lines))
(defn collect [format lines] (ingest format transform/collect lines))
