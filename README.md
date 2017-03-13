# postagga
[![License MIT](https://img.shields.io/badge/License-MIT-blue.svg)](http://opensource.org/licenses/MIT)
[![Gratipay](https://img.shields.io/gratipay/turbopape.svg)](https://gratipay.com/turbopape/)

> "But if thought corrupts language, language can also corrupt thought."
- George Orwell, 1984

**postagga** is a suite of tools that aims to assist you in generating a
simple and embeddable natural language processor. You can use **postagga** 
to process annotated text samples into full fledged parsers capable of understanding users' *free speech* input as structured data.

To achieve this, **postagga** extracts the *phrase structure* of your input, and tries to find how do this structure compare to its many semantic rules and if it finds a match, where in this structure shall he extract meaningful information.

Let's study a simple example. Look at the next sentence:

> "Rafik loves apples"

That is our "Natural language input"

First step in understanding this sentence is to extract some structure from it so it is easier to interpret. One common way to do this is extracting its grammatical phrase structure, which is close enough to what "function" words are actually meant to provide:

> Noun Verb Noun

That was the phrase structure analysis, or as we call it POS (Part Of Speech) Tagging. These "Tags" qualify parts of the sentence, as the name imply, and will be used as a hi-fidelity mechanism to write rules for parsers of such phrases.

**postagga** has tools that enable you to train POS Taggers for any language you want, without relying on external libs. Actually, it does not care about the meaning of the tags at all. However, you should be consistent and clear enough when annotating your input data samples with tags: On the one hand, your parser will be more reliable and on the other hand, of course, you'll do yourself a great favour maintaining your parser.

Now comes the parser part. Actually, **postagga** offers a parser that needs smantic **rules** to be able to map a particular phrase structure into data.In our example, we know that the first **Noun** depicts a subject carrying out some action. This action is  represented by the **Verb** following it.Finally, the **Noun** coming after the **Verb** will undergo this action.

**postagga** parsers just lets you express such rules so they can extract the data for you. You literally tell them to take the first **Noun**, call it **Subject**, take the verb, label it **action** and the last **Noun** will be the **Object**. and package all of it into the following data strucutre:

```clojure
{:Subject "Rafik" :Action "Loves" :Object "Apples"}
```
Naturally, **postagga** can handle much more complex sentences !

**postagga** parsers are eventually compiled into self-contained packages, with no single third party dependency, and can easily run  on servers (Clojure version) and on the browser (ClojureScript), so now your bots can really get what you're trying to tell them!

# Workflow

## Training a POS Tagger
First of all, you need to train a POS Tagger that can qualify parts of
your natural text. **postagga** relies on Hidden Markov Models,
discoverd by
the
[Viterbi  Algorithm](https://en.wikipedia.org/wiki/Viterbi_algorithm). This
algorithm relies on a set of matrices, like what states (the POS Tags)
we have, how likely do we transition from one POS tag to another,
etc...
All of these constitute a model. And these are computed out of what we
call an annotated text corpus. the **trainer** namespace create models
out of such annoateted text corpus.
To train a model, make sure you have an annotated corpus like so:

```clojure
[ ;; A vector of sentences like this one:
[["-" "PONCT"] ["guerre" "NC"] ["d'" "P"] ["indochine" "NPP"]] [["-" "PONCT"] ["colloque" "NC"] ["sur" "P"] ["les" "DET"] ["fraudes" "NC"]] [["-" "PONCT"] ["dernier" "ADJ"] ["résumé" "NC"] [":" "PONCT"] ["l'" "DET"] ["\"" "PONCT"] ["affaire" "NC"] ["des" "P+D"] ["piastres" "NC"] ["\"" "PONCT"]] [["catégories" "NC"] [":" "PONCT"] ["guerre" "NC"] ["d'" "P"] ["indochine" "NPP"] ["." "PONCT"]] [["indochine" "NPP"] ["française" "ADJ"] ["." "PONCT"]] [["quatrième" "ADJ"] ["république" "NC"] ["." "PONCT"]
;; etc...
]
```

say you have this corpus - that is : a vector of annotated sentences
in a var named corpus. To train a model, just issue:

```clojure
(require '[postagga.trainer :refer [train]]

(def model (train corpus)) ;;<- beware, these can be large vars so avoid realizing all of them like printing in your REPL !!!
```

We processed two annotated corpora for french:
- [postagga-sequoia-fr.edn](https://github.com/turbopape/postagga/blob/master/resources/postagga-sequoia-fr.edn)
    Generated from
    the
    [Sequoia Corpus from INRIA](https://www.rocq.inria.fr/alpage-wiki/tiki-index.php?page=CorpusSequoia).
    
- [postagga-tb-fr.edn](https://github.com/turbopape/postagga/blob/master/resources/fr_tb_v_model.edn)
    Generated from
    the
    [Free French tree Bank](https://github.com/nicolashernandez/free-french-treebank).
    
The suite of tools used to process these two corpora are in
the [corpuscule project](https://github.com/turbopape/corpuscule). 
Please refer to the licensing of these corpora to see to what
extent you can use derived work from them.

We already trained two models out of these two french corpora:
- [fr_sequoia_pos_v_model.edn](https://github.com/turbopape/postagga/blob/master/resources/fr_sequoia_pos_v_model.edn)
- [fr_tb_v_model.edn](https://github.com/turbopape/postagga/blob/master/resources/fr_tb_v_model.edn)
      
    
Now you can use the model to assign POS tags to speech:
(sentences must be fed in the form of a vector of all small-case
tokens):
```clojure
(require '[postagga.tagger :refer [viterbi]])

(viterbi model ["je" "suis" "heureux"])
;;=> ["CLS" "V" "ADJ"]
```

## Using the tagger to parse free speech

Now you have your tagger trained, you can use a parser to drill the
information from your sentences. For our last example, say you want
**postagga** to understand how you feel. It can do this by detecting
the first token as being a Subject - **CLS**, doing a verb - **V** and
then having an Adjective **ADJ**. We want to detect who is having what
adjective in our sentence.
For this, we'll use the **postagga.parser** namespace.
First of all, require the namespace:

```clojure
(require '[postagga.parser :refer [parse-tags-rules]])
```

Then, you'll need to specify rules for the parser. We want to grab the
word tagged as **CLS** and the word tagged as *ADJ* as our
infomation. Here's what the parser rules look like:

```clojure
(def sample-rules [{;;Rule TB French "je suis heureux."
                    :id :sample-rule-tb-french
                    :optional-steps []
                    :rule [:qui       ;;<----- A atep
                    #{:get-value #{"CLS"}} ;;<----- A state in the parse machine
                                           ;;i.e, a set of possible sets of POS TAGS                           
                    :mood
                    #{#{"V"}}
                    #{:get-value #{"ADJ"}}]}]
```
This deserves some explanation before we carry on with our example.

The parser is basically a state machine. It goes through steps, with each step encompassing multiple
states. A state basically refers to words; it is matched with tag sets
(A Word can have mutiple tags, if your preferred tagger wants to). 
Different tag sets can be assigned to a step. For instance, to say that in this state we want a Noun or an
Adjective, you can put:

```clojure
#{#{"V"} #{"NPP"}}
```

putting the keyword **:get-value** in a state grabs the word having
this state and puts in the yielded parse map, under a key representing
that step. for instance, the value of "V" will be reflected on the
output map as (given that we are in the **:qui** step):

```clojure
{:qui ["je"]}
```
If we had multiple get-values, we'll find multiple words in the
data-structure, that's why the map is referring a vector.

It is also possible to say that a state can be emcountered repeatedly,
using the :multi keyword. If you say in certain state:
```clojure
:some-step
;...
#{:get-value :multi #{"ADJ"}
;...
```
You'll find in the parse map:
```clojure
{:some-step ["beau" "laid" "intelligent"]}
```

**:optional-steps** tells the parser not to raise an error if a step
belonging to this vector is not present.

You'll also need to tell the parser how to break down a line of text
into a vector of words. We call this a **tokenizer**. Waiting to
develop a full fledged couple with language-specific rules, we can
just start by a naive one that splits strings using space characters:

```clojure
(def sample-tokenizer-fn #(clojure.string/split % #"\s"))
```

Back to our sample. With **sample-rules** holding a set of rules as defined above,
you can parse your sentence like so:

```clojure
(def parse-result (parse-tags-rules sample-tokenizer-fn fr-v-tb-pos-tagger-fn sample-rules  "je suis heureux"))
```
And you'd have a detailed result like so:

```clojure
{:errors nil ;;<- The error if any
 :result {:rule :sample-rule-tb-french ;; <- Which rule was detected 
          :data {:qui ["je"],          ;; <- The data structure drilled
                                       ;;    down from the input.
                 :mood ["heureux"]}}}
```

The errors will be reported as a collection mapping each rule to what
step and state did the parser fail. This can be quite large, so be
carefule not to spit the contents of the result directly in your REPL,
you can test on the **:errors** being _nil_ and work with the
**:data** value:

```clojure
;; Do something with
(:data parse-result)
 
```

# Complete list of features
You can see some of this workflow (other than the training) in the
[Tests](https://github.com/turbopape/postagga/blob/master/test/postagga/core_test.clj).

Please refer to the [Changelog](https://github.com/turbopape/postagga/blob/master/CHANGELOG.md) to see included features per version.

# Code Of Conduct

Please note that this project is released with a [Contributor Code of Conduct](./CODE_OF_CONDUCT.md). By
participating in this project you agree to abide by its terms.

# License and Credits

Copyright (c) 2017 [Rafik Naccache](mailto:rafik@fekr.tech).

Happily brought to you by [fekr](http://fekr.tech).

Distributed under the terms of the [MIT License]("http://opensource.org/licenses/MIT).

