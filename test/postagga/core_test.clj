;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.core-test
  (:require [clojure.test :refer :all]
            [postagga.tools :refer [load-edn-from-resource get-row get-column]]
            [postagga.parser :refer [parse-tags-rules]]
            [postagga.tagger :refer [viterbi]]))

;; Here we force the use of a model, but in trainer.clj we have the
;; means to create such a model

(def fr-seq-model (load-edn-from-resource "fr_sequoia_pos_v_model.edn"))
(def fr-tb-model (load-edn-from-resource "fr_tb_v_model.edn"))

(def sample-model ; as trained by train in trainer.clj
  {:states #{"P" "V" "N" "D"},
   :transitions {["P" "V"] 1.0, ["V" "D"] 1.0, ["D" "N"] 1.0},
   :emissions
   {["P" "Je"] 1.0,
    ["V" "Mange"] 0.5,
    ["V" "Tue"] 0.5,
    ["N" "Pomme"] 0.5,
    ["N" "Mouche"] 0.5,
    ["D" "Une"] 1.0},
   :init-probs {"P" 1.0}})

;; I can plug any pos-tagger, given a vector of words returns a vector or [w [pos-tags]]
;; Now we have a HMM based models, which is state of the art: the Viterbi Algorithm
(def sample-pos-tagger-fn (partial viterbi sample-model))


(def fr-v-tb-pos-tagger-fn (partial viterbi fr-tb-model))

;; Todo : create the tokenizer ns
(def sample-tokenizer-fn #(clojure.string/split % #"\s"))

(def sample-rules [{:id :sample-rule-0
                    :rule [:sujet
                           #{:get-value #{"P"}}

                           :action
                           #{:get-value #{"V"}}

                           :objet
                           #{#{"D"}}
                           #{:get-value #{"N"}}]}
                   
                   
                   {;;Rule 0 "Montrez moi les chaussures noires"
                    :id :sample-rule-1
                    :rule [:intent       ;;<----- A atep
                           #{:get-value #{"NPP"}}    ;;<----- A status in the parse machine (a set of possible sets of POS TAGS)
                           #{#{"NC"}}
                           
                           :product
                           #{#{"DET"}}
                           #{:get-value #{"NC"}} ;;<- I get this part of speech as a value, will find an entry :product ["Text"]

                           :qualif
                           #{:multi :get-value #{"ADJ"}}]} ;;<- multi: I can get several times this state
                   
                   {;;Rule 1 "Je cherche une montre analogique"
                    :id :sample-rule-2
                    :rule [:intent       ;;<----- A atep
                           #{#{"CLS"}}    ;;<----- A status in the parse machine (a set of possible sets of POS TAGS)
                           #{:get-value #{"V"}}
                           
                           :product
                           #{#{"DET"}}
                           #{:get-value #{"NC"}}
                           
                           :qualif
                           #{:multi :get-value #{"ADJ"}}]}
                   {;;Rule TB French "je suis heureux."
                    :id :sample-rule-tb-french
                    :rule [:qui       ;;<----- A atep
                           #{:get-value #{"CLS"}}    ;;<----- A status in the parse machine (a set of possible sets of POS TAGS)                           
                           :mood
                           #{#{"V"}}
                           #{:get-value #{"ADJ"}}]}
                   ])


(deftest sample-rules-pass
  (testing "Je tue une mouche doit retourner P V D N")
  (is (=  {:sujet["Je"] :action ["tue"], :objet ["pomme"]}
          (-> (parse-tags-rules sample-tokenizer-fn sample-pos-tagger-fn  sample-rules "Je tue une pomme" [])
              (get-in [:result :data])))))

(deftest fr-tb-rules-pass
  (testing " Je suis heureux doit retourner CLS V ADJ")
  (is (=  {:qui["je"] :mood ["heureux"]}
          (-> (parse-tags-rules sample-tokenizer-fn fr-v-tb-pos-tagger-fn sample-rules  "je suis heureux" [])
              (get-in [:result :data])))))




