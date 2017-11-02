(ns syrup.file.alpha
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [syrup.core.alpha :as core]
            [syrup.util.alpha :as util]))

(defn data-summary [item]
  (str (util/boxed (str "Item #" (inc (:data-index item))))
       "Raw data:\n\""
       (:data-line item)
       "\"\n\nParsed data:\n"
       (util/pretty (dissoc item :data-index :data-line :data-errors))))

(defn invalid-summary [error-summary item]
  (str (data-summary item)
       "\nErrors:\n"
       (->> item
            (:data-errors)
            (map error-summary)
            (distinct)
            (map #(str "  " %))
            (str/join "\n"))))

(defn valid-summary [item]
  (str (data-summary item)
       "\nValid!"))

(defn item-summary [error-summary item]
  (if (:data-errors item)
    (invalid-summary error-summary item)
    (valid-summary item)))

(defn validate [format error-summary path]
  (let [{:keys [items valid? count
                valid-count
                invalid-count
                data-errors-count format-error-count error-tally]
         :as result} (->> path
                          (io/reader)
                          (line-seq)
                          (core/collect format))]
    (if valid?
      (do (println (str "Parsed " count " items from file \"" path "\", all of which were valid."))
          true)
      (do (println (str "Parsed " count " items from file \"" path "\" - " valid-count " valid, " invalid-count " invalid."
                        \newline
                        \newline
                        (util/boxed "Error Tally")
                        (->> error-tally
                             (map (fn [{:keys [in pred count]}]
                                    (str "[" (last in) ", " (util/pred-name pred) "] " count)))
                             (str/join "\n"))
                        "\n\n"
                        (->> all
                             (map (partial item-summary error-summary))
                             (str/join "\n\n"))))
          false))))
