;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.tagger-test
  (:require [clojure.test :refer :all]
            [postagga.tagger :refer :all]))

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

(deftest viterbi-test
  (testing "viterbi")
  (is (= ["P" "V" "D" "N"] (viterbi sample-model ["Je" "Mange" "Une" "Pomme"]))))

