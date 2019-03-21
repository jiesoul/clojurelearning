(ns com.jiesoul.sicp.ex01
  (:require [com.jiesoul.sicp.ch01 :refer [prime? cube pi-term pi-next gcd fixed-point]]))

;; 1.2
(/ (+ 5 4 (- 2 (- 3 (+ 6 (/ 4 5)))))
   (* 3 (- 6 2) (- 2 7)))

;; 1.3
(defn max-three [a b c]
  (cond
    (or (> b a c) (> a b c)) (+ a b)
    (or (> c b a) (> b c a)) (+ b c)
    :else (+ c a)))

;; 1.4
(defn a-plus-abs-b [a b]
  ((if (> b 0) + 1) a b))

;; 1.5 采用正则序 p 会无限展开，

;; 1.6 因为使用应用序值 predicate 会无限展开

;; 1.9
(defn new-plus [a b]
  (if (zero? a)
    b
    (inc (+ (dec a) b))))


;; 1.10
(defn A [x y]
  (cond
    (zero? y) 0
    (zero? x) (* 2 y)
    (= y 1) 2
    :else (A (dec x) (A x (dec y)))))

;; 1.11
(defn f-11 [n]
  (if (< n 3)
    n
    (+ (f-11 (dec n)) (* 2 (f-11 (- n 2))) (* 3 (f-11 (- n 3))))))

(defn f-11-iter [n]
  (let [sum  (fn [a b c] (+ a (* 2 b) (* 3 c)))
        step (fn [a b c counter n]
               (cond
                 (< n 3) n
                 (> counter n) c
                 :else
                 (recur b c (sum a b c) (inc counter) n)))]
    (step 0 1 2 3 n)))

;; 1.12
(defn f-12 [n]
  (if (= n 1)
    [1]
    (let [new (f-12 (dec n))]
      (mapv + (conj new 0) (cons 0 new)))))


;; 1.16
(defn fast-expt-iter [b n]
  (let [step (fn [b n]
               (cond
                 (zero? n) 1
                 (= n 1) b
                 :else
                 (recur (* b b) (/ n 2))))]
    (if (even? n)
      (step b n)
      (* b (step b (dec n))))))

;; 1.17
(defn new-mul [a b]
  (if (zero? b)
    0
    (+ a (new-mul a (dec b)))))

(defn new-double [a]
  (+ a a))

(defn halve [a]
  (/ a 2))

(defn new-mul-expt [a b]
  (cond
    (zero? a) 0
    (= a 1) b
    (even? a) (new-mul-expt (halve a) (new-double b))
    :else
    (+ b (new-mul-expt (halve (dec a)) (new-double b)))))

;; 1.18
(defn new-mul-iter [a b]
  (let [step (fn [a b counter]
               (cond
                 (zero? a) counter
                 (= a 1) (+ b counter)
                 :else
                 (recur (halve a) (new-double b) counter)))]
    (if (even? a)
      (step a b 0)
      (step (dec a) b b))))

;; 1.19
(defn fib-iter [a b p q count]
  (cond
    (zero? count) b
    (even? count) (fib-iter a b p q (/ count 2))
    :else (fib-iter (+ (* b q) (* a q) (* a p))
            (+ (* b p) (* a q))
            p
            q
            (- count 1))))

(defn fib [n]
  (fib-iter 1 0 0 1 n))

;; 1.22

(defn report-prime-test [elapsed-time]
  (print " *** ")
  (print elapsed-time))

(defn start-prime-test [n start-time]
  (if (prime? n)
    (report-prime-test (- (System/currentTimeMillis) start-time))))

(defn timed-prime-test [n]
  (println)
  (print n)
  (start-prime-test n (System/currentTimeMillis)))

(defn search-for-primes [n]
  (loop [c 1]
    (if (and (> c n) (prime? c))
      c
      (recur (+ c 2)))))


;; 1.29
(defn xps-integral [f a b n]
  )

;; 1.30
(defn sum-iter [term a next-f b]
  (let [iter (fn [a result]
               (if (> a b)
                 result
                 (recur (next-f a) (+ result (term a)))))]
    (iter a 0)))


(defn sum-cubes-iter [a b]
  (sum-iter cube a inc b))

(defn sum-integers-iter [a b]
  (sum-iter identity a inc b))

(defn pi-sum-iter [a b]
  (sum-iter pi-term a pi-next b))

;; 1.31
(defn product [t-f a next-f b]
  (if (> a b)
    1
    (* (t-f a)
       (product
         t-f
         (next-f a)
         next-f
         b))))

(defn product-factorial [n]
  (product (fn [x] x) 1 #(inc %) n))

(defn pi-f [a]
  (if
    (odd? a)
    (/ (inc a) (+ a 2) 1.0)
    (/ (+ a 2) (inc a) 1.0)))

(defn product-pi [n]
  (product pi-f
           1
           inc
           n))

(defn product-iter [tf a nf b]
  (let [step (fn [a result]
               (if (> a b)
                 result
                 (recur (nf a) (* result (tf a)))))]
    (step a 1)))

(defn product-iter-factorial [n]
  (product-iter identity 1 inc n))

(defn product-iter-pi [n]
  (product-iter pi-f 1 inc n))


;; 1.32
(defn accumulate [combiner null-value term a next b]
  (if (> a b)
    null-value
    (combiner (term a)
              (accumulate combiner null-value term (next a) next b))))

(defn accumulate-sum-fac [a b]
  (accumulate + 0 identity a inc b))

(defn accumulate-product-factorial [n]
  (accumulate * 1 identity 1 inc n))

(defn accumulate-iter [combiner null-value term a next b]
  (let [step (fn [a result]
               (if (> a b)
                 result
                 (recur (next a) (combiner result (term a)))))]
    (step a null-value)))

;; 1.33
(defn filtered-accumulate [filtered combiner null-value term a next b]
  (let [step (fn [a result]
               (if (> a b)
                 result
                 (recur (next a) (if (filtered a)
                                   (combiner result (term a))
                                   result))))]
    (step a null-value)))

(defn filtered-accumulate-prime-sum [a b]
  (filtered-accumulate prime? + 0 identity a inc b))

(defn filtered-accumulate-product-gcd [n]
  (filtered-accumulate (fn [a]
                         (= (gcd a n) 1))
                       *
                       1
                       identity
                       1
                       inc
                       n))

;; 1.35
(defn f-35 []
  (fixed-point #(+ 1 (/ 1.0 %)) 1.0))

;; 1.37
(defn cont-frac [n d k]
  (let [step (fn step [i]
               (if (> i k)
                 0
                 (/ (n i) (+ (d i) (step (inc i))))))]
    (step 1)))

(defn cont-frac-iter [n d k]
  (let [step (fn [i])]))
