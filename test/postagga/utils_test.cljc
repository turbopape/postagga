;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.utils-test
  (:require [clojure.test :refer :all]
            [postagga.tools :refer :all]))

(deftest bigrams-test
  (testing "Bigrams")
  (is (= #{} (bigrams nil))))


