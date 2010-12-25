What's this
=============
ire is a library for incremental regular expression matching, based on further development of the ideas from Dan Piponi's famous blogpost [Fast Incremental Regular Expression Matching with Monoids](http://blog.sigfpe.com/2009/01/fast-incremental-regular-expression.html).

* **ire** is for "incremental", "regular", "expressions"
* **incremental** means "fast recomputation of results according to changes of input string" (not pattern)
* **regular** means "regular" - t.i. no backrefs or other Perl magic.

How to use it?
===============

Add the target/ire-VERSION.jar to your classpath.

    import net.ire.*;
    import net.ire.regex.RegexCompiler;

    // Compile the regexes
    String[] regexes = {...};
    PatternSet pat = RegexCompiler.compile(regexes);

    // Index a string (slow)
    IndexedString is = pat.match(someString);

    // Get matches (fast)
    for(Match m : is.getMatches()) {
        int startPos = m.startPos();
        int length = m.length();
        int whichPattern = m.whichPattern();
    }

    // Here's the "incremental" part. Assume 'a' and 'b' are IndexedString's.
    // You can cut and recombine string pieces, it will be fast, and getMatches()
    // of the resulting strings will be fast.
    IndexedString c = a.append(b);
    IndexedString sub = is.subSequence(start, end);
    Pair<IndexedString,IndexedString> p = is.splitBefore(i);

How to experiment with it?
==========================
Open the IDEA project (or create a project in your favourite IDE over it - there's just one library dependency in the "lib" folder) and run the "tests" in `net.ire.IntegrationTest`.

Do not forget to run the unit tests after changes.

Ask me (ekirpichov@gmail.com) if you're interested in something.

How fast is it?
===============

It is much faster than `java.util.regex` in the following case:

* Not too many patterns
* Not too many occurences of them
* The input strings are very long
* Incremental operations are dominant
* You have a lot of spare memory (the "block size" parameter is not too large)

It is much slower in most other cases.

For example, when finding all occurences of patterns from the ["regex-dna" benchmark](http://shootout.alioth.debian.org/u32q/performance.php?test=regexdna) 
in a 500-kb DNA string with total 100 occurences, with a block size of 16 we're getting nearly 5000 occurences per second with our library and about 500 per second with java.util.regex.

However, when solving the same problem for a 50kb string with 100 occurences, with a block size of 256 we have exactly the opposite - 500 vs 6000.

How does it work?
==================
Read Dan Piponi's aforementioned blogpost; here are the differences:

* Instead of fingertrees, we use a "rope" datastructure with caching sums of values in an arbitrary monoid. The rope datastructure is in `net.ire.rope` package. It uses a constant-height 2-3 tree of N..2N-1 array chunks. Append and split operations are quite trivial.
* We not only test for a match, but also find match positions. This is done by 1 split to find the end of the match and another to find the beginning, with some intricacies for overlapping matches. See `net.ire.DFAMatcher` class.
* We use NFA instead of DFA, because we care mostly about number of states (we have to compose transition tables) and state blow-up of DFAs is unacceptable. FAs are in `net.ire.fa` package.
* We do some optimization of the NFA to further reduce states, see `net.ire.regex.RegexCompiler`.
* We use a compact representation of NFA as a boolean matrix represented as a bitset, with fast multiplication, see `net.ire.fa.PowerIntTable`

