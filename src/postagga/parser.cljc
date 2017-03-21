;; Copyright(c) 2017 - [Rafik Naccache](rafik@fekr.tech)
;; Licensed under the terms of the MIT License

(ns postagga.parser
  (:require [postagga.tagger :refer [viterbi]]))

(defn matches?
  [input-item ; item : ["word" "postag"]
   current-tag-alternatives]
  (some #{(get input-item 1)} current-tag-alternatives))

(defn accept-tag
  "Verifies if an input like: [product NPP] correponds to
  one of the keys stored in the head of tag-stack, which would be an
  element like #{ :multi #{:Noun Value...}}, or if it is a checkpoint,
  to notify the caller to construct a part of the task. the :multi in some state
  head of stack means that this token can be met multiple times,
  causing the stack to keep it when ever we find an item corresponding
  to it, or consuming it an moving forward if the element correspond
  to the next status."
  [input-item
   tag-stack]

  (if-let [current-tag-alternatives (first tag-stack)] ;; #{[:noun :verb ]...}
    (cond
      (keyword? current-tag-alternatives) {:step current-tag-alternatives
                                           :new-stack (rest tag-stack)} 

      
      ;; It corrsponds to one of the alternatives
      (matches? input-item current-tag-alternatives) (if (contains? current-tag-alternatives :multi)
                                                       {:step false
                                                        :new-stack  tag-stack}
                                                       {:step false
                                                        :new-stack (rest tag-stack)})
      ;; If I'm here, input-tem does-not match the head of the stack. If the head contains
      ;; Soemthing :multi, let's see if its following status correspond to our item so we can move forward
      
      (contains? current-tag-alternatives :multi) (if (matches? input-item (second tag-stack))
                                                    {:step false
                                                     :new-stack (-> tag-stack rest rest)}
                                                    ;; nope, even the thing after the :multi stage doesn't match.
                                                    ;; that won't do.
                                                    false)
      :default false)
    false))

(defn fast-forward
  "Goes FFW in a tag-stack until it finds a step specification. "
  [tag-stack]
  (if (seq tag-stack)
    (if (keyword? (first tag-stack))
      tag-stack
      (recur (rest tag-stack)))
    nil))

(defn get-value?
  [stack-item]
  (contains? stack-item :get-value))


(defn parse-sentence-w-a-tag-stack
  [pos-tagger-fn
   sentence
   init-tag-stack
   optional-steps]
  (loop
      [input-items (mapv (fn[item postag] [item  #{postag}])
                         sentence
                         (pos-tagger-fn sentence))
       tag-stack init-tag-stack
       output-stack {}
       output {}]
    (if (and (seq input-items) (seq tag-stack))
      (let [input-item (first input-items)
            {:keys [step new-stack] :as accept?} (accept-tag input-item tag-stack)]
        (cond
          step (recur input-items
                      new-stack
                      {:step step :items []}
                      (if  (empty? (get output-stack :items))
                        output
                        (assoc output (get output-stack :step)
                               (get  output-stack :items))))
          
          (and (not accept?)
               (some #{(get output-stack :step)}
                     optional-steps)) (if-let [ffw-stack (fast-forward tag-stack)]
                                        (recur input-items
                                               ffw-stack
                                               output-stack
                                               output)
                                        {:error {:step (get output-stack :step)
                                                 :expected (first tag-stack)
                                                 :item input-item}})
          
          (not accept?) {:error {:output output
                                 :step (get output-stack  :step)
                                 :expected (first tag-stack) :item input-item}}
          
          :default (recur (rest input-items)
                          new-stack
                          (if (get-value? (first tag-stack))
                            (merge-with conj output-stack {:items (get  input-item 0)})
                            output-stack) 
                          output)))

      ;; either input or stack are empty here.
      (cond (or  (and (empty? input-items)
                      (empty? tag-stack))
                 (some #{(first tag-stack)} optional-steps)
                 
                 (contains? (first tag-stack) :multi)) {:error false
                                                        :result (assoc output (get output-stack :step)
                                                                       (get  output-stack :items))} ;; all good,
            (not (empty? input-items)) {:error "Unable to consume all input."
                                        :input input-items}
            (not (empty? tag-stack)) {:error "Input does not fulfill all of the tag-stack states."
                                      :tag-stack tag-stack}))))


(defn parse-tags-rules
  "Tries to parse the sentence according to rules (tag stacks). If it finds a
  match, will return it. else, it'll return the errors it found"
  [tokenizer-fn
   pos-tagger-fn
   rules
   sentence]
  (loop [rem-rules rules
         errors []]
    (if (seq rem-rules)
      (let  [cur-rule (first rem-rules)
             optional-steps (:optional-steps cur-rule)
             sentence-tokens (tokenizer-fn sentence)
             
             cur-parse-result (parse-sentence-w-a-tag-stack pos-tagger-fn sentence-tokens (:rule cur-rule) optional-steps)]
        (if-let [err (get cur-parse-result :error)]
          (recur (rest rem-rules)
                 (conj errors err))
          {:errors nil
           :result {:rule (:id cur-rule)
                    :data (get cur-parse-result :result)}}))
      {:errors errors})))

