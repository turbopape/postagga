# Change Log

## [0.2.6] - 2017-04-02
### Added
- Add French and English common names dictionary look-up - we patch
  final Viterbi tagger results with this dictionary to ensure these
  names are detected.
- Add patching samples in tests file.
- Use tries to efficiently look up dictionaries - may change.

## [0.2.5] - 2017-03-22
### Changed
- Moved models out of resourcs into a specific folder, so they are not
  packaged in the jar - light in clojurescript(in which you'd use the
  namespaces)
- changed var names in model so they can be used easily in :refer.

### Fixed
- edn... etc in the tools.cljc - now only in clj. (use :refer)
- using :refer in core_tests.cljc

## [0.2.4] - 2017-03-21
### Fixed
- arg-max was no more referred to in tagger.cljc. Switched to
  arg-max-m.

### Added
- Two cljs friendly models as cljc namespaces: en_fn_model.cljc and
  fr_tb_model.cljc.
- core_test is now a cljc file.

## [0.2.3] - 2017-03-19
### Added 
- Annotated english corpus based on the [Framenet Project](https://framenet.icsi.berkeley.edu/fndrupal/).

## [0.2.2] - 2017-03-13
### Fixed
- in tagger.cljc: removed an irrelevant destructuring.

## [0.2.1] - 2017-03-13
### Added
- Detailed workflow and references in Readme.

### Changed
- :optional-steps are now specified in the rules. No more passing in
  the parser functions.

### Fixed
- Fixed the Viterbi implemetntion. We don't need T2, we have the
  associated state as we work with Clojure maps - Simpler
  implementation and most of all - it works !
- Removed README warning.
- Wrote tests for actual HMM model based on free french treebank. Pass.

## [0.2.0] - 2017-03-11
### Added
- Annotated french corpus based on the [Sequoia Corpus from INRIA](https://www.rocq.inria.fr/alpage-wiki/tiki-index.php?page=CorpusSequoia).
- Annotated french corpus based on the [Free French tree Bank](https://github.com/nicolashernandez/free-french-treebank)
- Changed signature of the viterbi fn.

## [0.1.0] - 2017-03-10
### Added
- First commit: trainer, tagger, parser , rules and tools namespaces.
- Have a sample usage in tests.
- Readme, Changelog, Code of Conduct.
