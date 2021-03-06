(ns com.jiesoul.codewar.Slope-test
  (:require [clojure.test :refer :all])
  (:require [com.jiesoul.codewar.Slope :refer :all]))


(deftest slope-test
  (is (= (slope [19,3,20,3]) "0"))
  (is (= (slope [-7,2,-7,4]) "undefined"))
  (is (= (slope [10,50,30,150]) "5"))
  (is (= (slope [15,45,12,60]) "-5"))
  (is (= (slope [10,20,20,80]) "6"))
  (is (= (slope [-10,6,-10,3]) "undefined")))