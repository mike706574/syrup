(ns syrup.error.alpha
  (:require [syrup.util.alpha :as util]))

(defn parts
  [{:keys [pred val in] :as error}]
  (let [quot-val (str \" val \")
        key (last in)
        quot-key (str \" (name key) \")
        context (str quot-key " with value " quot-val)]
    (merge error {:quot-val quot-val
                  :key key
                  :quot-key quot-key
                  :context context})))

(defn record-length-mismatch [{:keys [in pred val]}]
  (when (and (= in [:data-line])
             (sequential? pred)
             (= (first pred) 'pancake.fixed-width/record-length-is?))
    (str "Invalid record; expected a record of length " (second pred) ", but got " (count val) ".")))

(defn cell-count-mismatch [{:keys [in pred val] :as error}]
  (when (and (= in [:data-cell])
             (sequential? pred)
             (= (first pred) 'pancake.delimited/cell-count-is?))
    (str "Invalid record; expected a record with " (second pred) " cells, but got " (count val) ".")))

(defn generic-summary [error]
  (let [error-parts (parts error)
        {:keys [pred via in val quot-val key quot-key context]} error-parts]
    (cond
      (= via [:tailor/to-double]) (str "Numeric field " context " is not a valid number.")
      (= via [:tailor/to-basic-iso-date]) (str "Date field " context " is not a valid date.")
      (= pred `double?) (str "Numeric field " context " is not a valid number.")
      (= pred `inst?) (str "Date field " context " is not a valid date.")
      :else (or (record-length-mismatch error)
                (cell-count-mismatch error)
                (str "Field " context " failed predicate \"" (util/pred-name pred) "\".")))))
