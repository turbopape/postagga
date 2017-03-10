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
**postagga** parsers just lets you express this. You literally tell him take the first **Noun**, call it **Subject**, take the verb, label it **action** and the last **Noun** will be the **Object**. At the end of the day, **postagga**, given the input sentence will issue the following data strucutre:

```clojure
{:Subject "Rafik" :Action "Loves" :Object "Apples"}
```
Naturally, **postagga** can handle much more complex sentences !

**postagga** parsers are eventually compiled into self-contained packages, with no third part dependencies, and can run on servers (Clojure version) or on the browser (ClojureScript), so now your bots can really get what you're trying to tell them!


# Code Of Conduct

Please note that this project is released with a [Contributor Code of Conduct](./CODE_OF_CONDUCT.md). By
participating in this project you agree to abide by its terms.

# License and Credits

Copyright (c) 2017 [Rafik Naccache](mailto:rafik@fekr.tech).

Happily brought to you by [fekr](http://fekr.tech).

Distributed under the terms of the [MIT License]("http://opensource.org/licenses/MIT).

