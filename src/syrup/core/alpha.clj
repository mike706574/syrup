(ns syrup.core.alpha
  (:refer-clojure :exclude [sequence])
  (:require [clojure.core :as core]
            [pancake.core :as pancake]
            [pancake.format :as format]
            [syrup.tally.alpha :as tally]
            [syrup.validation.alpha :as validation]))

(defn validator [format]
  (let [item-spec (:spec format)
        field-specs (format/value-specs format)]
    (cond
      (and item-spec field-specs) (validation/conform-and-validate item-spec field-specs)
      item-spec (validation/validate item-spec)
      field-specs (validation/conform-and-validate any? field-specs))))

(defn xform [format]
  (let [dropper (when-let [skip (:skip format)]
                  (drop skip))
        parser (pancake/parse format)
        validator (validator format)]
    (apply comp (filter identity [dropper parser validator]))))

(defn sequence
  ([format lines]
   (core/sequence (xform format) lines))
  ([format xf lines]
   (core/sequence (comp (xform format) xf) lines)))

(defn collect [format lines]
  (tally/tally (xform format) lines))
