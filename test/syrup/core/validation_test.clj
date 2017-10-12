(ns syrup.core.parsing-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [are deftest is]]
            [syrup.core.alpha :as syrup]))

(stest/instrument)

(defn minimum [x] (fn minimum [y] (>= y x)))

(s/def :domain/id string?)
(s/def :domain/amount (s/and double? #(> % 5.0)))

(s/def :domain/item (s/keys :req-un [:domain/id :domain/amount]))

(def delimited {:id "test-format"
                :type "delimited"
                :delimiter \|
                :description "Test format."
                :spec :domain/item
                :cells [{:id :id :index 0 :spec :tailor/to-trimmed}
                        {:id :amount :index 1 :spec :tailor/to-double}]})

(deftest fully-valid
  (is (= {:valid? true,
          :count 1,
          :valid-count 1,
          :invalid-count 0,
          :error-tally #{},
          :valid [{:data-index 0, :data-line "AAA|015", :id "AAA", :amount 15.0}],
          :invalid []}
         (syrup/collect delimited ["AAA|015"]))))

(deftest non-double-amount
  (is (= {:valid? false,
          :count 1,
          :valid-count 0,
          :invalid-count 1,
          :error-tally
          #{{:key :amount,
             :pred '(clojure.spec.alpha/conformer tailor.specs/to-double),
             :count 1}},
          :valid [],
          :invalid
          [{:data-index 0,
            :data-line "AAA|14X",
            :id "AAA",
            :amount "14X",
            :data-errors
            [{:path [],
               :pred '(clojure.spec.alpha/conformer tailor.specs/to-double),
               :val "14X",
               :via [:tailor/to-double],
               :in [],
               :key :amount}]}]}
         (syrup/collect delimited ["AAA|14X"]))))

(deftest amount-too-low
  (is (= {:valid? false,
          :count 1,
          :valid-count 0,
          :invalid-count 1,
          :error-tally
          #{{:key :amount,
             :pred '(clojure.spec.alpha/conformer tailor.specs/to-double),
             :count 1}},
          :valid [],
          :invalid
          [{:data-index 0,
            :data-line "AAA|14X",
            :id "AAA",
            :amount "14X",
            :data-errors
            [{:path [],
               :pred '(clojure.spec.alpha/conformer tailor.specs/to-double),
               :val "14X",
               :via [:tailor/to-double],
               :in [],
               :key :amount}]}]}
         (syrup/collect delimited ["AAA|001"]))))
