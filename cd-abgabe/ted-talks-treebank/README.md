###############################################################################
# NAIST/NTT TED Treebank                                                      #
#  v. 1.0 (2014-10-06)                                                        #
#  Documentation by Graham Neubig (neubig@is.naist.jp)                        #
###############################################################################

This is a TreeBank of syntactic trees of the content of TED talks.
All treebank annotation follows the Penn Treebank standard.
The included files are listed below:

* English sentences and subtitle files for each talk
Subtitles: en-srt/TITLE.srt
Sentences: en-sent/TITLE.sent

* English parse trees
en-mrg/TITLE.mrg

These are in the Penn Treebank format, with extra "QUOTE" tags. These "QUOTE"s
can be removed using the script/remove-quote.py script to recover the original
Penn Treebank format.

For convenience, we also include dependency trees in CoNLL format in:
en-dep/TITLE.dep

* Tokenized sentences corresponding to the parse trees
en-tok/TITLE.tok

* Time alignments of the tokenized sentences
en-time/TITLE.time

These are in the format "token|start-time|end-time", where times are in seconds.
Words with XXX|XXX as the time are unpronounced words such as punctuation. Words
beginning or ending with CCC are compound words, and should be combined with the
previous or next words respectively.

* Sentences and subtitles in many languages
Subtitles: multi-srt/LANG/TITLE.LANG.srt
Sentences: multi-sent/LANG/TITLE.LANG.sent

* The original video corresponding to the text
en-mp4/TITLE.mp4

* A file mapping subtitles into English sentences
srtid/TITLE.srtid
