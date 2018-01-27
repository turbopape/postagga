;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.trainer-test
  (:require [clojure.test :refer :all]
            [postagga.trainer :refer :all]))

(deftest process-annotated-sentence-test
  (testing "process-annotated-sentence")
  (is (= {:states #{}, :transitions {}, :emissions {}, :init-state nil} (process-annotated-sentence nil)))
  (is (= {:states #{}, :transitions {}, :emissions {}, :init-state nil} (process-annotated-sentence [])))
  (is (= {:states #{"P"}, :transitions {}, :emissions {["P" "Je"] 1}, :init-state "P"} (process-annotated-sentence [["Je" "P"]])))
  (is (= {:states #{"P" "V"}, :transitions {["P" "V"] 1}, :emissions {["P" "Je"] 1 ["V" "Mange"] 1}, :init-state "P"} (process-annotated-sentence [["Je" "P"] ["Mange" "V"]])))
  (is (= {:states #{"P" "V" "A"}, :transitions {["P" "V"] 1 ["V" "A"] 1}, :emissions {["P" "Je"] 1 ["V" "Mange"] 1 ["A" "Une"] 1}, :init-state "P"} (process-annotated-sentence [["Je" "P"] ["Mange" "V"] ["Une" "A"]])))
  (is (= {:states #{"P"}, :transitions {["P" "P"] 1}, :emissions {["P" "Je"] 2}, :init-state "P"} (process-annotated-sentence [["Je" "P"] ["Je" "P"]])))
  (is (= {:states #{"V" "N"}, :transitions {["V" "N"] 1}, :emissions {["V" "Montre"] 1 ["N" "Montre"] 1}, :init-state "V"} (process-annotated-sentence [["Montre" "V"] ["Montre" "N"]])))
  (is (= {:states #{"P" "V" "N"}, :transitions {["P" "P"] 1 ["P" "V"] 1 ["V" "P"] 1 ["P" "N"] 1}, :emissions {["P" "Je"] 1 ["P" "Te"] 1 ["V" "Montre"] 1 ["P" "Ma"] 1 ["N" "Montre"] 1}, :init-state "P"} (process-annotated-sentence [["Je" "P"] ["Te" "P"] ["Montre" "V"] ["Ma" "P"] ["Montre" "N"]]))))

(deftest compute-matrix-row-probs-test
  (testing "compute-matrix-row-probs")
  (is (= {} (compute-matrix-row-probs nil nil)))
  (is (= {} (compute-matrix-row-probs #{} nil)))
  (is (= {} (compute-matrix-row-probs #{} {})))
  (is (= {} (compute-matrix-row-probs nil {})))
  (is (= {} (compute-matrix-row-probs #{"P"} {})))
  (is (= {} (compute-matrix-row-probs #{} {["P" "P"] 2})))
  (is (= {["P" "P"] 1.0} (compute-matrix-row-probs #{"P"} {["P" "P"] 2})))
  (is (= {["P" "P"] 0.5 ["P" "V"] 0.5 ["V" "P"] 0.25 ["V" "V"] 0.75} (compute-matrix-row-probs #{"P" "V"} {["P" "P"] 1 ["P" "V"] 1 ["V" "P"] 1 ["V" "V"] 3}))))
