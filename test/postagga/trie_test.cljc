;;Copyright (c) 2017 Rafik Naccache <rafik@fekr.tech>
;;Distributed under the MIT License
(ns postagga.trie-test
  (:require [clojure.test :refer :all]
            [postagga.trie :refer :all]))

(deftest build-trie-test
  (testing "Build Trie")
  (is (= {} (build-trie [])))
  (is (= {"h" {:nb 1, :next {"e" {:nb 1, :next {"l" {:nb 1, :next {"l" {:nb 1, :next {"o" {:nb 1, :end true}}}}}}}}}} (build-trie ["hello"])))
  (is (= {"h" {:nb 2, :next {"e" {:nb 2, :next {"l" {:nb 2, :next {"l" {:nb 1, :next {"o" {:nb 1, :end true}}} "p" {:nb 1, :end true}}}}}}}} (build-trie ["hello","help"])))
  (is (= {"h" {:nb 2, :next {"e" {:nb 2, :next {"l" {:nb 2, :next {"l" {:nb 2, :end true, :next {"o" {:nb 1, :end true}}} }}}}}}} (build-trie ["hello","hell"]))))

