(ns syrup.core.parsing-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [are deftest is]]
            [syrup.core.alpha :as syrup]))

(stest/instrument)

(def delimited-no-header {:id "test-format"
                          :type "delimited"
                          :delimiter \|
                          :description "Test format."
                          :cells [{:id :id :index 0}
                                  {:id :amount :index 1}]})

(deftest collecting-delimited-no-header
  (is (= {:valid? true,
          :count 1,
          :valid-count 1,
          :invalid-count 0,
          :error-tally #{},
          :items
          [{:data-index 0, :data-line "AAA|015", :id "AAA", :amount "015"}]}
         (syrup/collect delimited-no-header ["AAA|015"]))))

(def delimited-with-header {:id "test-format"
                            :skip 1
                            :type "delimited"
                            :delimiter \|
                            :description "Test format."
                            :cells [{:id :id :index 0}
                                    {:id :amount :index 1}]})

(deftest collecting-delimited-with-header
  (is (=   {:valid? true,
            :count 1,
            :valid-count 1,
            :invalid-count 0,
            :error-tally #{},
            :items
            [{:data-index 0, :data-line "AAA|015", :id "AAA", :amount "015"}]}
         (syrup/collect delimited-with-header ["ID|AMOUNT"
                                               "AAA|015"]))))

(def fixed-width {:id "test-format"
                  :type "fixed-width"
                  :description "Test format."
                  :fields [{:id :id :start 1 :end 3}
                           {:id :amount :start 4 :end 6}]})

(deftest collecting-fixed-width
  (is (= {:valid? true,
          :count 2,
          :valid-count 2,
          :invalid-count 0,
          :error-tally #{},
          :items
          [{:data-index 0, :data-line "AAA015", :id "AAA", :amount "015"}
           {:data-index 1, :data-line "BBB123", :id "BBB", :amount "123"}]}

         (syrup/collect fixed-width ["AAA015" "BBB123"]))))
