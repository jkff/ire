package net.ire;

import org.jetbrains.annotations.Nullable;

/**
 * Created on: 22.07.2010 23:20:48
 */
public interface IndexedString<S extends IndexedString> extends CharSequence {
    Iterable<Match> getMatches();

    Pair<S,S> splitBefore(int index);

    @Nullable 
    Pair<S,S> splitAfterRise(Predicate<S> pred);

    S prepend(char c);
    S append(char c);
    S append(S s);

    S subSequence(int start, int end);

    IndexedString<S> reverse();
    
    boolean traverseWith(CharIteratee i);
}
