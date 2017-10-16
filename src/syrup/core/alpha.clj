(ns syrup.core.alpha
  (:require [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [pancake.core :as pancake]
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
        validator (cond
                    (and record-spec field-specs) (validation/conform-and-validate record-spec field-specs)
                    record-spec (validation/validate record-spec)
                    field-specs (validation/conform-and-validate any? field-specs)
                    :else nil)
        xf (if validator
             (comp parser validator)
             parser)]
    (collect xf lines)))

(defn validate [format lines] (ingest format transform/summarize lines))
(defn collect [format lines] (ingest format transform/collect lines))
