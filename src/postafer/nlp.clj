(ns postafer.nlp
  (:require [clojure.java.io :as io])
  (:import [java.io File FileInputStream]
           [opennlp.tools.tokenize TokenizerME TokenizerModel]
           [opennlp.tools.sentdetect SentenceDetectorME SentenceModel]
           [opennlp.tools.postag POSTaggerME POSModel]
           [opennlp.tools.chunker ChunkerME ChunkerModel]))


;; TODO: Turn these to macros?
(defn make-tokenizer
  [resource-token-model]
  (with-open [modelIn (java.io.FileInputStream.
                       (-> resource-token-model io/resource io/file str))]
    (let [model (TokenizerModel. modelIn)
          tokenizer (TokenizerME. model)]
      #(.tokenize tokenizer %1))))

(defn make-sentdetect
  [resource-sentdetect-model]
  (with-open [modelIn (java.io.FileInputStream.
                       (-> resource-sentdetect-model io/resource io/file str))]
    (let [model (SentenceModel. modelIn)
          sentdetect (SentenceDetectorME. model)]
      #(.sentDetect sentdetect %1))))

(defn make-pos-tagger
  [resource-pos-model]
  (with-open [modelIn (java.io.FileInputStream.
                       (-> resource-pos-model io/resource io/file str))]
    (let [model (POSModel. modelIn)
          tagger (POSTaggerME. model)]
      #(.tag tagger %1))))

(defn make-chunker
  [resource-ch-model]
  (with-open [modelIn (java.io.FileInputStream.
                       (-> resource-ch-model io/resource io/file str))]
    (let [model (ChunkerModel. modelIn)
          chunker (ChunkerME. model)]
      #(.chunk chunker %1 %2))))
