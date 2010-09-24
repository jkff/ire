package net.ire;

import net.ire.util.Function2;
import net.ire.util.Pair;
import net.ire.util.Predicate;
import org.jetbrains.annotations.Nullable;

/**
 * Created on: 22.07.2010 23:20:48
 */
public interface IndexedString extends CharSequence {
    Iterable<Match> getMatches();

    Pair<IndexedString,IndexedString> splitBefore(int index);

    @Nullable
    <ST> Pair<IndexedString,IndexedString> splitAfterRise(
            ST seed,
            Function2<ST,IndexedString,ST> addChunk, Function2<ST,Character,ST> addChar,
            Predicate<ST> toBool);

    /**
     * Like splitAfterRise, but we count from the right end.
     * @param addChunk will be given a NON-REVERSED chunk
     */
    @Nullable
    <T> Pair<IndexedString,IndexedString> splitAfterBackRise(
            T seed,
            Function2<T,IndexedString,T> addChunk, Function2<T,Character,T> addChar,
            Predicate<T> toBool);

    IndexedString append(IndexedString s);

    IndexedString subSequence(int start, int end);
}
