(ns syrup.file.alpha
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [syrup.core.alpha :as core]
            [syrup.util.alpha :as util]
            [tailor.transform :as transform]
            [tailor.validation :as validation]))

(defn data-summary [record]
  (str (util/boxed (str "Record #" (:data-index record)))
       "Raw data:\n\""
       (:data-line record)
       "\"\n\nParsed data:\n"
       (util/pretty (dissoc record :data-index :data-line :data-errors))))

(defn invalid-summary [error-summary record]
  (str (data-summary record)
       "\nErrors:\n"
       (->> record
            (:data-errors)
            (map error-summary)
            (distinct)
            (map #(str "  " %))
            (str/join "\n"))))

(defn validate [format error-summary path]
  (let [{:keys [valid? count
                valid valid-count
                invalid invalid-count
                data-errors-count format-error-count error-tally]
         :as result} (->> path
                          (io/reader)
                          (line-seq)
                          (core/collect format))]
    (if valid?
      (do (println (str "Parsed " count " records from file \"" path "\", all of which were valid."))
          true)
      (do (println (str "Parsed " count " records from file \"" path "\" - " valid-count " valid, " invalid-count " invalid."
                        \newline
                        \newline
                        (util/boxed "Error Tally")
                        (->> error-tally
                             (map (fn [{:keys [in pred count]}]
                                    (str "[" (last in) ", " (util/pred-name pred) "] " count)))
                             (str/join "\n"))
                        "\n\n"
                        (->> invalid
                             (map (partial invalid-summary error-summary))
                             (str/join "\n\n"))))
          false))))
