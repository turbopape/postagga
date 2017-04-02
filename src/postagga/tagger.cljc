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
        
        T1 (loop [rem-observations (rest observations)
                  prev-observation (first observations)
                  rem-states states
                  T1 (into {} (for [i states]
                                [[i (first observations)] ((fnil  * 0 0)
                                                           (init-probs i)
                                                           (emissions [i (first observations)]))]))]

             (if (seq rem-observations)
               (let [cur-observation (first rem-observations)]
                                        ; I still have states to test...
                 (if (seq rem-states)
                   ;;I go to the next state for this observation
                   (let [cur-state (first rem-states)

                         Akj (get-column-m transitions cur-state)
                         
                         T1ki-1 (get-column-m T1 prev-observation)
                         
                         A*T (merge-with * Akj T1ki-1)]
                     
                     (recur rem-observations
                            prev-observation
                            (rest rem-states)
                            (assoc T1 [cur-state cur-observation] (*
                                                                   (if-let [p  (get emissions
                                                                                    [cur-state cur-observation])]
                                                                     p
                                                                     0)
                                                                   (reduce max (vals A*T))))))
                   ;; No more states, I Go to the next Observation, I resume from the first state
                   (recur  (rest rem-observations)
                           cur-observation
                           states
                           T1)))

               T1))]

    (loop [rem-observations  observations
           res []]
      (if (seq rem-observations)             
        (recur (rest rem-observations)
               (conj res (-> (get-column-m T1 (first rem-observations))
                             arg-max-m
                             (get 0))))
        (into [] res)))))


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
