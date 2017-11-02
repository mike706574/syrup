(ns syrup.validation.validate-test
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [syrup.validation.alpha :as validation]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]))

(s/def ::id (s/and string? (complement str/blank?)))
(s/def ::rate double?)
(s/def ::date inst?)

(s/def ::item (s/keys :req [::id ::rate ::date]))

(def iso-date (parsers/date "yyyyMMdd"))

(deftest valid
  (is (= [{::id "foo"
           ::rate 2.5
           ::date (iso-date "19950112")}]
         (validation/validate ::item [{::id "foo"
                                       ::rate 2.5
                                       ::date (iso-date "19950112")}]))))

(deftest invalid?
  (is (= [{:syrup.validation.validate-test/id " ",
            :syrup.validation.validate-test/rate "x",
            :syrup.validation.validate-test/date "01x21995",
            :data-errors
            [{:path [:syrup.validation.validate-test/id],
              :pred `(complement str/blank?)
              :val " ",
              :via [:syrup.validation.validate-test/item :syrup.validation.validate-test/id],
              :in [:syrup.validation.validate-test/id]}
             {:path [:syrup.validation.validate-test/rate],
              :pred `double?,
              :val "x",
              :via [:syrup.validation.validate-test/item :syrup.validation.validate-test/rate],
              :in [:syrup.validation.validate-test/rate]}
             {:path [:syrup.validation.validate-test/date],
              :pred `inst?,
              :val "01x21995",
              :via [:syrup.validation.validate-test/item :syrup.validation.validate-test/date],
              :in [:syrup.validation.validate-test/date]}]}]
         (validation/validate ::item [{::id " "
                                       ::rate "x"
                                       ::date "01x21995"}]))))
