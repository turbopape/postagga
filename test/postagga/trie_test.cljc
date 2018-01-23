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

(deftest completions-test
  (testing "Completsions")
  (let [trie (build-trie [])]
     (is (= nil (completions trie nil)))     ; TODO: maybe it would be better if result was []
     (is (= nil (completions trie "")))      ; TODO: maybe it would be better if result was []
     (is (= nil (completions trie "hello"))))     ; TODO: maybe it would be better if result was []
  (let [trie (build-trie ["hello"])]
     (is (= ["hello"] (completions trie nil)))
     (is (= ["hello"] (completions trie "")))
     (is (= ["hello"] (completions trie "h")))
     (is (= ["hello"] (completions trie "he")))
     (is (= ["hello"] (completions trie "hel")))
     (is (= ["hello"] (completions trie "hell")))
     (is (= nil (completions trie "hello")))    ; TODO: maybe this should be ["hello"]? (Or at leat [])
     (is (= nil (completions trie "world"))))    ; TODO: maybe [] would be better...
  (let [trie (build-trie ["hello" "hell" "help"])]
     (is (= ["hell" "help" "hello"] (completions trie nil)))
     (is (= ["hell" "help" "hello"] (completions trie "")))
     (is (= ["hell" "help" "hello"] (completions trie "h")))
     (is (= ["hell" "help" "hello"] (completions trie "he")))
     (is (= ["hell" "help" "hello"] (completions trie "hel")))
     (is (= ["hello"] (completions trie "hell"))) ; TODO: maybe "hell" should be included too
     (is (= nil (completions trie "hello")))    ; TODO: maybe this should be ["hello"]? (Or at leat [])
     (is (= nil (completions trie "world"))))    ; TODO: maybe [] would be better...
  (let [trie (build-trie ["hello" "world"])]
     (is (= ["world" "hello"] (completions trie nil)))
     (is (= ["world"] (completions trie "wo")))))
