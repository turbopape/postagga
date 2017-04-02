(ns postagga.trie)

(defn insert-str-in-trie
  [trie the-string]
  (let [str-seq (map str the-string)]
    ;;=> we transform the string into a seq of
    ;;   chars.
    (loop [remaining-chars str-seq
           result trie
           path []]
      (if (seq remaining-chars)
        (let [path-in-node (conj path
                                 (first remaining-chars))]
          ;;=> we get the path to the node representing
          ;;   the state referred by this char in this
          ;;   level
          (recur (rest remaining-chars)
                 ;;=>  we carry on with the rest of the chars
                 (update-in result
                            path-in-node
                            #(merge-with + % {:nb 1}))
                 ;; and  we update / insert a node referred by this char,
                 ;; updating :nb, the number of tims the sequence thus far
                 ;; has appeared in our input
                 (conj path-in-node :next)))
                 ;; => the path for descending is the value
                 ;;    referred by :next key
        (update-in result (pop path) merge {:end true})))))
        ;;=> end of recursion, we return the trie with
        ;;   an :end key set at true, to tell that this
        ;;   state represents a whole string
     
(defn descend-branch
  [branch]
  (loop [cur-branch branch
         cur-result ""
         results []]
    (if (nil? cur-branch)
      results
      ;;=> end of recursion
      (let [c-key (if (vector? cur-branch)
                    ;; as we're applying this to
                    ;; map entries, we sometimes have to do
                    ;; with a vector [k v]
                    (get cur-branch 0)
                    ;; and sometimes with a map
                    (first (keys cur-branch)))]
        ;;=> This determines the key being processed for both cases
        (recur (if (vector? cur-branch )
                 (:next  (get cur-branch 1))
                 (get-in cur-branch [c-key :next]))
               ;;=> sem logic, be it a vector or a map,
               ;;   we recur over the next element in
               ;;   the structure
               (str cur-result c-key)
               ;;=> we append the character to a current result buffer
               (let [new-cur-branch (if (vector? cur-branch)
                                      (get cur-branch 1 )
                                      (get cur-branch c-key))]
                 ;;=> next branch is also determined
                 ;;for both cases: a vector or a map
                 (if (:end new-cur-branch)
                   ;;=> if :end at true, i.e, this state represents a whole world
                   ;;   we add current result buffer to the whole results vector
                   ;;   along with the number of times it occured
                   (conj results {:result (str  cur-result c-key)
                                  :nb (get new-cur-branch :nb)})
                   results)))))))

(defn completions
  [trie the-string]
  (let [str-seq (map str the-string)
        from-path (into []  (interleave str-seq (repeat :next) ))
        ;; => given an input string, we know that we'll
        ;;    start descending our tree from path [c1 :next c2 :next ...]
        rest-commons (loop [common-branch (get-in trie from-path )
                            r-commons []]
                       ;;=> rest-commons is the result of phase 1
                       ;;   computation
                       (if (> (count common-branch) 1)
                         r-commons
                         ;;=> we have reached a fork. the keys discovered
                         ;;   so far can be added to the starting path
                         ;;   computed out of input string.
                         (if  (nil? common-branch)
                           []
                           ;;=> else we never forked. Thus far, we are at the
                           ;;   end of the trie. No forks were available,
                           ;;   so  we return nothing and we let
                           ;;   descend-branch compute all the overlapping
                           ;;   completions, as we are in a one strictly
                           ;;   sequantial branch
                           (let [c-key (first (keys common-branch))]
                             (recur (get-in common-branch [c-key :next])
                                    (conj r-commons c-key))))))
        ;;=> else we recur building our r-commons vector
        ;;   descending the tree jumping :next by :next
        rest-commons-path (interleave rest-commons (repeat :next))
        ;;=> the path to add to begin with (if we found any forks)
        str-commons (reduce str rest-commons)
        ;;=> the string that lead to the forks if any.
        possible-completions  (mapcat descend-branch
                                      (get-in trie
                                              (into from-path
                                                    rest-commons-path)))]
    ;;=> mapping descend branch over the branches
    ;;   determined by adding the path to the fork
    ;;   if any is present, or from the simple path
    ;;   computed out of the input string suffices as
    ;;   we are in a single way path.   
    (if (not (empty?  possible-completions))
      (into [] (map #(str the-string
                          str-commons
                          (get  % :result))
                    (sort-by :nb >=  possible-completions)))
      ;;=> We issue possible completions sorted by their
      ;;   frequency in the input
      (if (not (empty? str-commons))
          [(str the-string ;;"|"
                str-commons)]
          nil
          ))))
;;=> descend-branch did not yield anything, we return
;;   the common-string as a completion (if we found any)


(defn build-trie
[entities]
  (loop [rentities entities
         result {}]
    (if (seq rentities)
      (recur (rest rentities)
             (insert-str-in-trie result (first rentities)))
      result)))
