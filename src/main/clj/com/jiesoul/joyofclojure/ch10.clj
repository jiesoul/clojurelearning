(ns com.jiesoul.joyofclojure.ch10
  (:import java.util.concurrent.Executors
           java.util.concurrent.locks.ReadWriteLock
           (java.util.concurrent.locks ReentrantLock)))

(def thread-pool
  (Executors/newFixedThreadPool
    (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(defn dothreads!
  [f & {thread-count :threads
        exec-count   :times
        :or          {thread-count 1, exec-count 1}}]
  (dotimes [t thread-count]
    (.submit thread-pool
             #(dotimes [_ exec-count]
                (f)))))

(dothreads! #(.print System/out "Hi ") :thread-count 2 :times 2)

(def initial-board
  [[:- :k :-]
   [:- :- :-]
   [:- :K :-]])

(defn board-map [f board]
  (vec (map #(vec (for [s %] (f s)))
            board)))

(defn reset-board!
  []
  (def board (board-map ref initial-board))
  (def to-move (ref [[:K [2 1]] [:k [0 1]]]))
  (def num-moves (ref 0)))

(defn neighbors
  ([size yx] (neighbors [[-1 0] [1 0] [0 -1] [0 1]]
                        size
                        yx))
  ([deltas size yx]
    (filter (fn [new-yx]
              (every? #(< -1 % size) new-yx))
            (map #(vec (map + yx %))
                 deltas))))

(def king-moves
  (partial neighbors
           [[-1 -1] [-1 0] [-1 1] [0 -1] [0 -1] [0 1] [1 -1] [1 0] [1 1]] 3))

(defn good-move?
  [to enemy-sq]
  (when (not= to enemy-sq)
    to))

(defn choose-move
  [[[mover mpos] [_ enemy-pos]]]
  [mover (some #(good-move? % enemy-pos)
               (shuffle (king-moves mpos)))])

(reset-board!)
(take 5 (repeatedly #(choose-move @to-move)))

(defn place [from to] to)

(defn move-piece [[piece dest] [[_ src] _]]
  (alter (get-in board dest) place piece)
  (alter (get-in board src) place :-)
  (alter num-moves inc))

(defn update-to-move [move]
  (alter to-move #(vector (second %) move)))

(defn make-move []
  (let [move (choose-move @to-move)]
    (dosync (move-piece move @to-move))
    (dosync (update-to-move move))))

(reset-board!)

(make-move)
(board-map deref board)
(make-move)
(board-map deref board)
(board-map deref board)

(dothreads! make-move :threads 100 :times 100)
(board-map deref board)

(io! (.println System/out "Haikeeba!"))
;(dosync (io! (.println System/out "Haikeeba!")))

(defn make-move-v2
  []
  (dosync
    (let [move (choose-move @to-move)]
      (move-piece move @to-move)
      (update-to-move move))))
(reset-board!)
(make-move)
(board-map deref board)
@num-moves
(dothreads! make-move-v2 :threads 100 :times 100)
(board-map #(dosync (deref %)) board)
@to-move
@num-moves

(defn move-piece [[piece dest] [[_ src] _]]
  (commute (get-in board dest) place piece)
  (commute (get-in board src) place :-)
  (commute num-moves inc))
(reset-board!)
(dothreads! make-move-v2 :threads 100 :times 100)
(board-map deref board)
@to-move

(defn update-to-move [move]
  (commute to-move #(vector (second %) move)))
(dothreads! make-move-v2 :threads 100 :times 100)
(board-map #(dosync (deref %)) board)
@to-move

(dosync (ref-set to-move '[[:K [2 1]] [:K [0 1]]]))

(defn strees-ref [r]
  (let [slow-tries (atom 0)]
    (future
      (dosync
        (swap! slow-tries inc)
        (Thread/sleep 200)
        @r)
      (println (format "r is: %s, history: %d, after: %d tries @r"
                       @r (.getHistoryCount r) @slow-tries)))
    (dotimes [i 500]
      (Thread/sleep 10)
      (dosync (alter r inc)))
    :done))

(strees-ref (ref 0))
(strees-ref (ref 0 :max-history 30))
(strees-ref (ref 0 :min-history 15 :max-history 30))

;; agent
(def joy (agent []))
(send joy conj "first edition")
@joy

(defn slow-conj [coll item]
  (Thread/sleep 1000)
  (conj coll item))

(send joy slow-conj "Second Edition")
@joy

(def log-agent (agent 0))

(defn do-log [msg-id message]
  (println msg-id ":" message)
  (inc msg-id))

(defn do-step [channel message]
  (Thread/sleep 1)
  (send-off log-agent do-log
            (str channel message)))

(defn three-step [channel]
  (do-step channel " ready to begin (step 0)")
  (do-step channel " warming up (step 1)")
  (do-step channel " really getting going new (step 2)")
  (do-step channel " done! (step 3)"))

(defn all-together-now []
  (dothreads! #(three-step "alpha"))
  (dothreads! #(three-step "beta"))
  (dothreads! #(three-step "omega")))

(all-together-now)
@log-agent

(do-step "important: " "this must go out")
(await log-agent)
(send log-agent (fn [_] 1000))
(do-step "epsilon " "near miss")

(defn exerise-agents [send-fn]
  (let [agents (map #(agent %) (range 10))]
    (doseq [a agents]
      (send-fn a (fn [_] (Thread/sleep 1000))))
    (doseq [a agents]
      (await a))))

(time (exerise-agents send-off))
(time (exerise-agents send))

(send log-agent (fn [_] 3000))
@log-agent
(agent-error log-agent)
;@log-agent
(restart-agent log-agent 2500 :clear-actions true)

(send-off log-agent do-log "The agent, it lives!")
(restart-agent log-agent 2500 :clear-actions true)

(defn handle-log-error [the-agent the-err]
  (println "An action sent to the log-agent throw " the-err))

(set-error-handler! log-agent handle-log-error)
(set-error-mode! log-agent :continue)

(send log-agent (fn [x] (/ x 0)))
(send log-agent (fn [] 0))
(send-off log-agent do-log "Stayin' alive, stayin' alive...")

(def ^:dynamic *time* (atom 0))
(defn tick [] (swap! *time* inc))
(dothreads! tick :threads 1000 :times 100)
@*time*

(defn manipulable-memoize [function]
  (let [cache (atom {})]
    (with-meta
      (fn [& args]
        (or (second (find @cache args))
            (let [ret (apply function args)]
              (swap! cache assoc args ret)
              ret)))
      {:cache cache})))

(def slowly (fn [x] (Thread/sleep 1000) x))
(time [(slowly 9) (slowly 9)])

(def sometimes-slowly (manipulable-memoize slowly))

(time [(sometimes-slowly 108) (sometimes-slowly 108)])

(meta sometimes-slowly)
(let [cache (:cache (meta sometimes-slowly))]
  (swap! cache dissoc `(108)))

(meta sometimes-slowly)
(time [(sometimes-slowly 108) (sometimes-slowly 108)])

(defn print-read-only []
  (println "*read-eval* is currently" *read-eval*))

(defprotocol SafeArray
  (aset-1 [this i f])
  (aget-1 [this i])
  (count-1 [this])
  (seq-1 [this]))

(defn make-dumb-array [t sz]
  (let [a (make-array t sz)]
    (reify
      SafeArray
      (count-1 [_] (count a))
      (seq-1 [_] (seq a))
      (aget-1 [_ i] (aget a i))
      (aset-1 [this i f]
        (aset a i (f (aget-1 this i)))))))

(defn pummel [a]
  (dothreads! #(dotimes [i (count-1 a)] (aset-1 a i inc))
    :threads 100))
(def D (make-dumb-array Integer/TYPE 8))
(pummel D)
(seq-1 D)

(defn make-safe-array [t sz]
  (let [a (make-array t sz)]
    (reify
      SafeArray
      (count-1 [_] (count a))
      (seq-1 [_] (seq a))
      (aget-1 [_ i]
        (locking a (aget a i)))
      (aset-1 [this i f]
        (locking a
          (aset a i (f (aget-1 this i))))))))

(def D (make-safe-array Integer/TYPE 8))
(pummel D)
(seq-1 D)

(defn lock-i [target-index num-locks]
  (mod target-index num-locks))

(defn make-smart-array [t sz]
  (let [a (make-array t sz)
        Lsz (/ sz 2)
        L (into-array (take Lsz (repeatedly #(ReentrantLock.))))]
    (reify
      SafeArray
      (count-1 [_] (count a))
      (seq-1 [_] (seq a))
      (aget-1 [_ i]
        (let [lk (aget L (lock-i (inc i) Lsz))]
          (.lock lk)
          (try
            (aget a i)
            (finally (.unlock lk)))))
      (aset-1 [this i f]
        (let [lk (aget L (lock-i (inc i) Lsz))]
          (.lock lk)
          (try
            (aset a i (f (aget-1 this i)))
            (finally (.unlock lk))))))))

(def S (make-smart-array Integer/TYPE 8))
(pummel S)
(seq-1 S)
*read-eval*
(var *read-eval*)
#'*read-eval*

(defn print-read-eval []
  (println "*read-eval* is currently" *read-eval*))

(defn binding-play []
  (print-read-eval)
  (binding [*read-eval* false]
    (print-read-eval))
  (print-read-eval))

(binding-play)

(def favorite-color :green)
favorite-color
(var favorite-color)

(def x 42)
{:out-var-value x
 :with-locals (with-local-vars [x 9]
                {:local-var x
                 :local-var-value (var-get x)})}

(with-precision 4
  (/ 1M 3))
(/ 1M 3)
(with-precision 4
  (map (fn [x] (/ x 3)) (range 1M 4M)))
(with-precision 4
  (doall (map (fn [x] (/ x 3)) (range 1M 4M))))
(with-precision 4
  (map (bound-fn [x] (/ x 3)) (range 1M 4M)))