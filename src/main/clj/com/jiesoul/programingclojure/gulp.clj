(ns com.jiesoul.programingclojure.gulp
  (:import (java.io FileInputStream InputStreamReader BufferedReader)))

(defn gulp [src]
  (let [sb (StringBuffer.)]
    (with-open [reader (-> src
                           FileInputStream.
                           InputStreamReader.
                           BufferedReader.)]
      (loop [c (.read reader)]
        (if (neg? c)
          (str sb)
          (do
            (.append sb (char c))
            (recur (.read reader))))))))

(defn make-reader [src]
  (-> src
      FileInputStream.
      InputStreamReader.
      BufferedReader.))

