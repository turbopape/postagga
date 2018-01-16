;; Copyright(c) 2017 - [Rafik Naccache](rafik@fekr.tech)
;; Distributed under the MIT License.
;; A POS Tagger based on the [Viterbi Algorithm](https://en.wikipedia.org/wiki/Viterbi_algorithm)

(ns postagga.tagger
  (:require [postagga.tools :refer [get-column-m get-row-m arg-max-m are-close-within? find-first]]
            [postagga.trie :refer [completions]]))

(defn viterbi
  "- states -  in NLP : the tags : [P V ADJ] 
  - intial-probs - je pars de quel mot initialement ? {V 0.2 P 0.3} ...
  - transition-matrix - avec quell proba on va d'un etat i a  etat j : {[p v] 0.1 [p adj] 0.2...} 
  - emission-matrix - avec quell proba  on a le mot (obseravtion) j si on a le tag (etat) i: {[ p Je] 0.9  [V viens] 0.3 ...} 
  ------------- These are the trained model ---------
  - observations - in NLP: tokens represnting the sentence to be pos-tagged  [je mange ...]"
  [model observations]
  (let [{:keys [states 
                init-probs 
                transitions 
                emissions]} model
        
        [T1 T2] (loop [rem-observations (rest observations)
                  prev-observation (first observations)
                  rem-states states
                  observations-idx 1
                  T1 (into {} (for [i states]
                                [[i 0] ((fnil  * 0 0)
                                                           (init-probs i)
                                                           (emissions [i (first observations)]))]))
                  T2 (into (sorted-map) (for [state states] [[state 0] nil]))]
            (if (seq rem-observations)
               (let [cur-observation (first rem-observations)]
                                        ; I still have states to test...
                 (if (seq rem-states)
                   ;;I go to the next state for this observation
                   (let [cur-state (first rem-states)

                         Akj (get-column-m transitions cur-state)
                         
                         T1ki-1 (get-column-m T1 (dec observations-idx))
                         
                         A*T (into (sorted-map) (for [state states] [state (* (get Akj [state cur-state] 0) (get T1ki-1 [state (dec observations-idx)] 0))]))]
                    (recur rem-observations
                            prev-observation
                            (rest rem-states)
                            observations-idx
                            (assoc T1 [cur-state observations-idx] (*
                                                                   (if-let [p  (get emissions
                                                                                    [cur-state cur-observation])]
                                                                     p
                                                                     0)
                                                                   (reduce max (vals A*T))))
                            (assoc T2 [cur-state observations-idx] (key (apply max-key val A*T)))))
                   ;; No more states, I Go to the next Observation, I resume from the first state
                   (recur  (rest rem-observations)
                           cur-observation
                           states
                           (inc observations-idx)
                           T1
                           T2)))

               [T1 T2]))
        last-state-with-max-prob (first (arg-max-m (get-column-m T1 (dec (count observations)))))]
       (loop [act-state last-state-with-max-prob
              states []
              index (dec (count observations))]
             (if (<= 0 index) (recur (get (get-column-m T2 index) [act-state index]) (conj states act-state) (dec index))
              (reverse states)))))


(defn patch-w-entity
  "Looks in a entities-db dictionary, if the word in sentence is close enough > threshold
  patch it with this target-tag, regardless
  entities-db being a trie, we must call completions and choose the longest one"
  
  [threshold sentence entities-db-trie tags target-tag]
  {:pre [(= (count sentence)
            (count tags))
         (vector? sentence)
         (vector? tags)]}
  (loop [words sentence
         rtags tags
         result-tags []]

    (if (seq words)
      (recur (rest words)
             (rest rtags)
             (conj result-tags (if-let [entity? (find-first
                                                 #(are-close-within? threshold (first words) %)
                                                 (completions entities-db-trie (first words)))]
                                 target-tag
                                 (first rtags))))
      result-tags)))
