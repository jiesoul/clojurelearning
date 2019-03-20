(ns com.jiesoul.sicp.ex01-test
  (:require [clojure.test :refer :all])
  (:require [com.jiesoul.sicp.ex01 :refer :all]))

(deftest f-12-test
  (testing "test"
    (is (= (f-12 1) [1]))
    (is (= (f-12 2) [1 1]))
    (is (= (f-12 3) [1 2 1]))
    (is (= (f-12 4) [1 3 3 1]))
    (is (= (f-12 5) [1 4 6 4 1]))
    ))

(deftest max-three-test
  (testing "test 1.3"
    (is (= (max-three 1 2 3) 5))
    (is (= (max-three 2 1 3) 5))
    (is (= (max-three 2 3 1) 5))
    ))

(deftest f-11-iter-test
  (testing "test 1.11"
    (is (= (f-11-iter 0) 0))
    (is (= (f-11-iter 1) 1))
    (is (= (f-11-iter 2) 2))
    (is (= (f-11-iter 3) 8))
    (is (= (f-11-iter 4) 29))
    (is (= (f-11-iter 5) 105))
    ))

(deftest fast-expt-iter-test
  (testing "ex1.16"
    (is (= (fast-expt-iter 2 4) 16))
    (is (= (fast-expt-iter 2 3) 8))
    (is (= (fast-expt-iter 3 1) 3))
    (is (= (fast-expt-iter 3 0) 1))
    ))

(deftest new-mul-iter-test
  (testing "1.17"
    (is (= (new-mul-iter 4 5) 20))
    (is (= (new-mul-iter 0 5) 0))
    (is (= (new-mul-iter 5 0) 0))
    (is (= (new-mul-iter 5 5) 25))
    ))

(deftest timed-prime-test-test
  (testing "1.22"
    (timed-prime-test 5)
    ))

(deftest search-for-primes-test
  (testing "search-for-primes"
    (time (search-for-primes 1000))
    (time (search-for-primes 10000))
    (time (search-for-primes 100000))
    (time (search-for-primes 1000000))
    ))

(deftest sum-cubes-iter-test
  (testing "sum-iter"
    (is (= (sum-cubes-iter 1 10) 3025))
    (is (= (sum-integers-iter 1 10) 55))
    (is (= (* 8 (pi-sum-iter 1 1000)) 3.139592655589782))
    ))
