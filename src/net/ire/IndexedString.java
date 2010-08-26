package net.ire;

import net.ire.util.Function2;
import net.ire.util.Pair;
import net.ire.util.Predicate;
import org.jetbrains.annotations.Nullable;

/**
 * Created on: 22.07.2010 23:20:48
 */
public interface IndexedString<S extends IndexedString> extends CharSequence {
    Iterable<Match> getMatches();

    Pair<S,S> splitBefore(int index);

    @Nullable
    <ST> Pair<S,S> splitAfterRise(
            ST seed,
            Function2<ST,S,ST> addChunk, Function2<ST,Character,ST> addChar,
            Predicate<ST> toBool);

    /**
     * Like splitAfterRise, but we count from the right end.
     * @param addChunk will be given a NON-REVERSED chunk
     */
    @Nullable
    <T> Pair<S,S> splitAfterBackRise(
            T seed,
            Function2<T,S,T> addChunk, Function2<T,Character,T> addChar, 
            Predicate<T> toBool);

    S append(S s);

    S subSequence(int start, int end);
}
