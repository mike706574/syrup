(ns syrup.error.generic-summary-test
  (:require [syrup.error.alpha :as error]
            [clojure.test :refer [are deftest is]]))

(deftest record-length-mismatch
  (is (= "Invalid record; expected a record of length 6, but got 2."
         (error/generic-summary {:in [:data-line]
                                 :pred '(pancake.fixed-width/record-length-is? 6)
                                 :val "AB"}))))

(deftest delimited-length-mismatch
  (is (= "Invalid record; expected a record with 2 cells, but got 1."
         (error/generic-summary {:in [:data-cell]
                                 :pred '(pancake.delimited/cell-count-is? 2)
                                 :val ["A"]}))))
