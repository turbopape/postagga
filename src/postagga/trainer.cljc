;; copyright 2017 - rafik@fekr.tech

;; A trainer for the Viterbi algorithm
;; given an annotated corpus, i.e: lines like these:
#_[["Je" "P"] ["Mange" "V"] ["Une" "D"] ["Pomme" "N"]]
;; yields the viterbi params: 
;; states what are the states of our HMMs:
#_ #{"P" "V" "D" "N"}
;; initial probs - how likely are we sure that our first observation yields this:
#_{"P" 0.7 "V" 0.1}
;; transition matrix - given a state, how likely are we transition to the next one:
#_{["P" "V"] 0.8 ["V" "D"] 0.1}
;; emission matrix - given a state, how likely are we going to see this obseravtion for it:
#_{["P" "il"] 1 ["V" "mange"] 1} 

(ns postagga.trainer
  (:require [postagga.tools :refer [get-row]]))

(defn process-annotated-sentence
  " A sentence is [[Je P]] ... "
  [annotated-sentence]
  (loop [prev-token (first annotated-sentence)
         rem-tokens (-> annotated-sentence rest)
         states #{(get prev-token 1)}
         transitions {}
         emissions {[(get prev-token 1) (get prev-token 0)] 1}]
    
    (if (seq rem-tokens)
      (let [cur-token (first rem-tokens)]
        (recur cur-token
               (rest rem-tokens)
               (conj states (get cur-token 1))
               (merge-with + transitions {[(get prev-token 1) (get cur-token 1)] 1})
               (merge-with + emissions {[(get cur-token 1) (get cur-token 0)] 1})))
      
      {:states states
       :transitions transitions
       :emissions emissions
       :init-state (-> annotated-sentence
                       first
                       (get 1))})))

(defn compute-matrix-row-probs
  "transitions or observations: {[from to1] 2 [fro to2] 1 [from to3] 1} 
  => {[P D] 0.66 [P V] 0.3 [D N] 1}"
  [states matrix]
  (loop [rem-states states
         res {}]
    (if (seq rem-states)
      (let [cur-state (first rem-states)
            trans-from-cur-state (get-row matrix cur-state)
            nb-from-cur-state (reduce + (vals  trans-from-cur-state))]
        
        (recur (rest rem-states)
               (->> trans-from-cur-state
                    (map (fn [[k v]] [k (float (/ v nb-from-cur-state))]))
                    (into res))))
      res)))



(defn train
  "Takes an annotated-corpus (sentences):
  [[[Je P] [Mange V] [Une D] [Pomme N]] 
   [[Je P] [Tue V] [Une D] [Mouche N]]]
  
  And yields a map containing the viterbi HMM discovery algorithms paramters:
  
  {:states #{P V N D},
  :transitions {[P V] 1.0, [V D] 1.0, [D N] 1.0},
  :emissions
  {[P Je] 1.0,
  [V Mange] 0.5,
  [V Tue] 0.5,
  [N Pomme] 0.5,
  [N Mouche] 0.5,
  [D Une] 1.0},
  :init-probs {P 1.0}}"
  
  [annotated-sentences]
  (loop [rem-sentences annotated-sentences
         res-states #{}
         res-transitions {}
         res-emissions {}
         res-init-states []]
    
    (if (seq rem-sentences)
      (let [sentence (first rem-sentences)
            {:keys [states transitions emissions init-state] :as cur-sent-data} (process-annotated-sentence sentence)
            _ (println "emissions" emissions)]
        (recur (rest rem-sentences)
               (into res-states states)
               (merge-with + res-transitions transitions)
               (merge-with + res-emissions emissions)
               (conj res-init-states init-state)))
      {:states res-states
       :transitions (compute-matrix-row-probs res-states res-transitions)
       :emissions (compute-matrix-row-probs res-states res-emissions)
       :init-probs  (let [total-init-probs (count res-init-states)]
                      (reduce (fn[m k]
                                (merge-with + m {k (float  (/ 1 total-init-probs))}))
                              {}
                              res-init-states))})))
