;; copyright 2017 - rafik@fekr.tech
;; A POS Tagger based on the [Viterbi Algorithm](https://en.wikipedia.org/wiki/Viterbi_algorithm)

(ns postafer.tagger
  (:require [postafer.tools :refer [get-column-m]]))

(defn arg-max
  "Applies the fn to maximize to a map {:k1 val :k2 val...} then
  gives the key that yields the maximum value"
  [coll]
  (apply max-key (into [coll] (keys coll))))


(defn viterbi
  "- states -  in NLP : the tags : [P V ADJ] 
  - intial-probs - je pars de quel mot initialement ? {V 0.2 P 0.3} ...
  - transition-matrix - avec quell proba on va d'un etat i a  etat j : {[p v] 0.1 [p adj] 0.2...} 
  - emission-matrix - avec quell proba  on a le mot (obseravtion) j si on a le tag (etat) i: {[ p Je] 0.9  [V viens] 0.3 ...} 
  ------------- These are the trained model ---------
  - observations - in NLP: tokens represnting the sentence to be pos-tagged  [je mange ...]"
  [states 
   initial-probs 
   transition-matrix 
   emission-matrix
   observations]
  (let [[T1 T2] (loop [rem-observations (rest observations)
                       prev-observation (first observations)
                       rem-states states
                       T1 (into {} (for [i states]
                                     [[i (first observations)] ((fnil  * 0 0)
                                                                (initial-probs i)
                                                                (emission-matrix [i (first observations)]))]))
                       T2 (into {} (for [i states] [[i (first observations)] 0]))]

                  (if (seq rem-observations)
                    (let [cur-observation (first rem-observations)]
                                        ; I still have states to test...
                      (if (seq rem-states)
                        ;;I go to the next state for this observation
                        (let [cur-state (first rem-states)

                              Akj (get-column-m transition-matrix cur-state)
                            
                              T1ki-1 (get-column-m T1 prev-observation)
                            
                              A*T (merge-with * Akj T1ki-1)]
                          
                          (recur rem-observations
                                 prev-observation
                                 (rest rem-states)
                                 (assoc T1 [cur-state cur-observation] (*
                                                                        (if-let [p  (get emission-matrix
                                                                                         [cur-state cur-observation])]
                                                                          p
                                                                          0)
                                                                        (reduce max (vals  A*T))))
                                 (assoc T2 [cur-state cur-observation] (get (arg-max A*T) 0))))
                        ;; No more states, I Go to the next Observation, I resume from the first state
                        (recur  (rest rem-observations)
                                cur-observation
                                states
                                T1
                                T2))) ;; End of observations, I return
                    [T1 T2]))]

    (loop [rem-observations (reverse observations)
           X (-> (get-column-m T1 (last observations))
                 arg-max
                 (get 0))
           res '()]
      
      (if (seq rem-observations)             
        (recur (rest rem-observations)
               (get T2 [X (first rem-observations)])
               (conj res X))
        (into [] res)))))
