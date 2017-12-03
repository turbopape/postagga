;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.utils-test
  (:require [clojure.test :refer :all]
            [postagga.tools :refer :all]))

(deftest bigrams-test
  (testing "Bigrams")
  (is (= #{} (bigrams nil)))
  (is (= #{} (bigrams "")))
  (is (= #{} (bigrams "a")))
  (is (= #{"ab"} (bigrams "ab")))
  (is (= #{"ab" "bc"} (bigrams "abc"))))

(deftest similarity-test
  (testing "Similarity")
  (is (= 0 (similarity nil nil)))  ; TODO: should this really be the case
  (is (= 0 (similarity "" "")))    ; TODO: should this really be the case
  (is (= 0 (similarity "" nil)))
  (is (= 0 (similarity "a" nil)))
  (is (= 0 (similarity "a" "")))
  (is (= 0 (similarity "a" "a")))   ; TODO: should this really be the case???
  (is (= 1 (similarity "ab" "ab")))
  (is (= 1/2 (similarity "ab" "abc")))
  (is (= 1/3 (similarity "ab" "abcd")))
  (is (= 0 (similarity "ab" "cd"))))

