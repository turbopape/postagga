;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.tagger-test
  (:require [clojure.test :refer :all]
            [postagga.tagger :refer :all]))

(def sample-model 
  {:states #{"P" "V" "N" "D"},
   :transitions {["P" "V"] 0.3333333, ["P" "P"] 0.3333333, ["P" "N"] 0.3333334, ["V" "D"] 0.5, ["V" "P"] 0.5, ["D" "N"] 1.0 ["N" "N"] 1.0},
   :emissions
   {["P" "Je"]         0.3333333,
    ["P" "Te"]         0.3333333,
    ["P" "Ma"]         0.3333334,
    ["V" "Mange"]      0.3333333,
    ["V" "Tue"]        0.3333333,
    ["V" "Montre"]     0.3333334,
    ["N" "Pomme"]      0.3333333,
    ["N" "Mouche"]     0.3333333,
    ["N" "Montre"]     0.3333334,
    ["D" "Une"]        1.0},
:init-probs {"P" 1.0}})

(deftest viterbi-test
  (testing "viterbi")
  (is (= ["P" "V" "D" "N"] (viterbi sample-model ["Je" "Mange" "Une" "Pomme"])))
  (is (= ["P" "P" "V" "P" "N"] (viterbi sample-model ["Je" "Te" "Montre" "Ma" "Montre"]))))


(def sample-db-trie {"h" {:nb 1, :next {"e" {:nb 1, :next {"l" {:nb 1, :next {"l" {:nb 1, :next {"o" {:nb 1, :next {"a" {:nb 1 :end true}}}}}}}}}}}})

(deftest patch-w-entity-test
  (testing "patch-w-entity")
  (is (= [] (patch-w-entity 0.5 [] {} [] nil)))
  (is (= ["N"] (patch-w-entity 0.0 ["hello"] {} ["N"] "NPP")))
  (is (= ["NPP"] (patch-w-entity 0.5 ["hello"] sample-db-trie ["N"] "NPP")))
  (is (= ["N"] (patch-w-entity 1.0 ["hello"] sample-db-trie ["N"] "NPP")))
  (is (= ["N" "NPP"] (patch-w-entity 0.5 ["world" "hello"] sample-db-trie ["N" "N"] "NPP"))))

