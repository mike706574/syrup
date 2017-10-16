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

(defn generic-summary [error]
  (let [{:keys [pred via in quot-val key quot-key context]} (parts error)]
    (cond
      (= via [:tailor/to-double]) (str "Numeric field " context " is not a valid number.")
      (= via [:tailor/to-basic-iso-date]) (str "Date field " context " is not a valid date.")
      (= pred `double?) (str "Numeric field " context " is not a valid number.")
      (= pred `inst?) (str "Date field " context " is not a valid date.")
      :else (str "Field " context " failed predicate \"" (util/pred-name pred) "\"."))))
