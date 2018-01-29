;In this country soldiers are poor but they need a certain level of secrecy for their communications so, though they do not know Caesar cypher, they reinvent it in the following way.
;
;They use ASCII, without really knowing it, but code only letters a-z and A-Z. Other caracters are kept such as.
;
;They change the "rotate" each new message. This "rotate" is a prefix for their message once the message is coded. The prefix is built of 2 letters, the second one being shifted from the first one by the "rotate", the first one is the first letter, after being downcased, of the uncoded message.
;
;For example if the "rotate" is 2, if the first letter of the uncoded message is 'J' the prefix should be 'jl'.
;
;To lessen risk they cut the coded message and the prefix in five pieces since they have only five runners and each runner has only one piece.
;
;If possible the message will be evenly split between the five runners; if not possible, parts 1, 2, 3, 4 will be longer and part 5 shorter. The fifth part can have length equal to the other ones or shorter. If there are many options of how to split, choose the option where the fifth part has the longest length, provided that the previous conditions are fulfilled. If the last part is the empty string don't put this empty string in the resulting array.
;
;For example, if the coded message has a length of 17 the five parts will have lengths of 4, 4, 4, 4, 1. The parts 1, 2, 3, 4 are evenly split and the last part of length 1 is shorter. If the length is 16 the parts will be of lengths 4, 4, 4, 4, 0. Parts 1, 2, 3, 4 are evenly split and the fifth runner will stay at home since his part is the empty string and is not kept.
;
;Could you ease them in programming their coding?
;
;Example with shift = 1 :
;
;message : "I should have known that you would have a perfect answer for me!!!"
;
;code : => ["ijJ tipvme ibw", "f lopxo uibu z", "pv xpvme ibwf ", "b qfsgfdu botx", "fs gps nf!!!"]
;
;By the way, maybe could you give them a hand to decode?
;
;Caesar cipher : see Wikipedia
;

(defn char-shift
  [c n]
  (let [d (int c)
        m (+ d n)
        a (int \a)
        z (int \z)
        A (int \A)
        Z (int \Z)]
    (cond
      (and (>= d A) (<= d Z)) (char (if (> m Z) (- m 26) m))
      (and (>= d a) (<= d z)) (char (if (> m z) (- m 26) m))
      :else
      c
      )))

(defn encode-str [s shift]
  (let [coll (seq s)
        fv (first (seq (.toLowerCase (str (first coll)))))
        prefix (conj [] fv (char-shift fv shift))
        xs (concat prefix (map (comp #(char-shift % shift)) coll))]
    (letfn [(trim-gen [xs]
              (let [c (count xs)
                    q (quot c 5)
                    g (if (zero? (rem c 5)) q (inc q))
                    zxs (map #(apply str %) (partition-all g xs))]
                zxs))]
      (trim-gen xs))))

(defn decode [s]
  (let [s (apply str s)
        fv (first s)
        sv (second s)
        ss (rest (rest s))
        shift (- (int sv) (int fv))]
    (apply str (map #(char-shift % (- 26 (rem shift 26))) ss))))

(def message "I should have known that you would have a perfect answer for me!!!")

(def code ["ijJ tipvme ibw", "f lopxo uibu z", "pv xpvme ibwf ", "b qfsgfdu botx", "fs gps nf!!!"])

(def u "How can we become the kind of people that face our fear and do it anyway?")
(def v ["hiIpx dbo xf cf","dpnf uif ljoe p","g qfpqmf uibu g","bdf pvs gfbs bo","e ep ju bozxbz?"])