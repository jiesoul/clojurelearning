(ns com.jiesoul.sicp.ex02-test
  (:require [clojure.test :refer :all])
  (:require [com.jiesoul.sicp.ex02 :refer :all]))

(deftest make-rat-real-test
  (is (= (make-rat-real -1 2) [-1 2]))
  (is (= (make-rat-real 1 -2) [-1 2]))
  (is (= (make-rat-real -1 -2) [1 2]))
  (is (= (make-rat-real 1 2) [1 2]))

  )

(deftest midpoint-segment-test
  (testing "midpoint-segment-test"
    (is (= (midpoint-segment (make-segment (make-point 1 1) (make-point -1 -1))) [0 0]))
    (is (= (midpoint-segment (make-segment (make-point 2 1) (make-point -2 -1))) [0 0]))
    (is (= (midpoint-segment (make-segment (make-point 1 1) (make-point 3 3))) [2 2]))
    ))

(deftest make-rectangle-test
  (testing "sss"
    (is (= (make-rectangle (make-segment (make-point 1 1) (make-point -1 -1)))
           [[1 1] [1 -1] [-1 -1] [-1 1]]))
    ))
