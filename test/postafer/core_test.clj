(ns postafer.core-test
  (:require [clojure.test :refer :all]
            [postafer.core :refer :all]
            [postafer.parser :refer [parse-tags-rules]]
            [postafer.tagger :refer [viterbi]]))

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

(def sample-pos-tagger-fn (partial viterbi
                                   (:states sample-model)
                                   (:init-probs sample-model)
                                   (:transitions sample-model)
                                   (:emissions sample-model)))

(def sample-tokenizer-fn #(clojure.string/split % #"\s"))

(def sample-rules [{:id :sample-rule
                    :rule [:sujet
                           #{:get-value #{"P"}}

                           :action
                           #{:get-value #{"V"}}

                           :objet
                           #{#{"D"}}
                           #{:get-value #{"N"}}]}])

(deftest sample-rules-pass
  (testing "Je tue une mouche doit retourner P V D N")
  (is (=  {:sujet["Je"] :action ["tue"], :objet ["pomme"]}
         (-> (parse-tags-rules sample-tokenizer-fn sample-pos-tagger-fn sample-rules "Je tue une pomme" [])
             (get-in [:result :data])))))

