(ns syrup.core.alpha
  (:require [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [pancake.core :as pancake]
            [pancake.format :as format]
            [tailor.analysis :as analysis]
            [tailor.validation :as validation]))

(defn validator [format]
  (let [record-spec (:spec format)
        field-specs (format/value-specs format)]
    (cond
      (and record-spec field-specs) (validation/conform-and-validate record-spec field-specs)
      record-spec (validation/validate record-spec)
      field-specs (validation/conform-and-validate any? field-specs))))

(defn xform [format]
  (let [dropper (when-let [skip (:skip format)]
                  (drop skip))
        parser (pancake/parse format)
        validator (validator format)]
    (apply comp (filter identity [dropper parser validator]))))

(defn sequence
  ([format lines]
   (sequence (xform format) lines))
  ([format xf lines]
   (sequence (comp (xform format) xf) lines)))

(defn collect [format lines]
  (analysis/categorize-and-tally (xform format) lines))
