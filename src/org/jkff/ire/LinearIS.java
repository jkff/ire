package org.jkff.ire;

import org.jkff.ire.fa.BiDFA;
import org.jkff.ire.fa.DFA;
import org.jkff.ire.fa.TransferFunction;
import org.jkff.ire.fa.State;
import org.jkff.ire.util.Pair;
import org.jkff.ire.util.Reducer;
import org.jkff.ire.util.Function2;
import org.jkff.ire.util.Predicate;

/**
 * Created on: 23.07.2010 9:23:42
 */
public class LinearIS<ST extends State> implements DFAIndexedString<ST> {
    private CharSequence cs;
    private BiDFA<Character, ST> bidfa;
    private TransferFunction<ST> forward;
    private TransferFunction<ST> backward;

    public LinearIS(CharSequence cs, BiDFA<Character, ST> bidfa) {
        this(cs, bidfa, transferForward(bidfa, cs), transferBackward(bidfa, cs));
    }

    private LinearIS(CharSequence cs,
                     BiDFA<Character, ST> bidfa,
                     TransferFunction<ST> forward, TransferFunction<ST> backward) 
    {
        this.cs = cs;
        this.bidfa = bidfa;
        this.forward = forward;
        this.backward = backward;
    }

    public TransferFunction<ST> getForward() {
        return forward;
    }

    public TransferFunction<ST> getBackward() {
        return backward;
    }

    public Iterable<Match> getMatches() {
        return DFAMatcher.getMatches(bidfa, this);
    }

    public int length() {
        return cs.length();
    }

    public char charAt(int index) {
        return cs.charAt(index);
    }

    public String toString() {
        return cs.toString();
    }

    public LinearIS<ST> subSequence(int start, int end) {
        return new LinearIS<ST>(cs.subSequence(start, end), bidfa);
    }

    public Pair<IndexedString, IndexedString> splitBefore(int index) {
        return Pair.of(
                (IndexedString)new LinearIS<ST>(cs.subSequence(0, index), bidfa),
                (IndexedString)new LinearIS<ST>(cs.subSequence(index, cs.length()), bidfa));
    }

    public <T> Pair<IndexedString, IndexedString> splitAfterRise(
            T seed,
            Function2<T, IndexedString, T> addChunk,
            Function2<T, Character, T> addChar, Predicate<T> toBool)
    {
        T t = seed;
        for(int i = 0; i < length(); ++i) {
            if(toBool.isTrueFor(t))
                return splitBefore(i);
            t = addChar.applyTo(t, this.charAt(i));
        }
        if(toBool.isTrueFor(t))
            return splitBefore(length());
        return null;
    }

    public <T> Pair<IndexedString, IndexedString> splitAfterBackRise(
            T seed,
            Function2<T, IndexedString, T> addChunk, Function2<T, Character, T> addChar,
            Predicate<T> toBool)
    {
        T t = seed;
        for(int i = length()-1; i >= 0; --i) {
            if(toBool.isTrueFor(t))
                return splitBefore(i+1);
            t = addChar.applyTo(t, this.charAt(i));
        }
        return null;
    }

    public IndexedString append(IndexedString other) {
        return new LinearIS<ST>(cs.toString() + other.toString(), bidfa);
    }

    private static <ST extends State> TransferFunction<ST> transferForward(
            BiDFA<Character, ST> bidfa, CharSequence cs)
    {
        DFA<Character,ST> dfa = bidfa.getForward();
        Reducer<TransferFunction<ST>> reducer = dfa.getTransferFunctionsReducer();
        TransferFunction<ST> res = null;
        for(int i = 0; i < cs.length(); ++i) {
            res = reducer.compose(res, dfa.transfer(cs.charAt(i)));
        }
        return res;
    }

    private static <ST extends State> TransferFunction<ST> transferBackward(
            BiDFA<Character, ST> bidfa, CharSequence cs) {
        DFA<Character, ST> dfa = bidfa.getBackward();
        Reducer<TransferFunction<ST>> reducer = dfa.getTransferFunctionsReducer();
        TransferFunction<ST> res = null;
        for(int i = cs.length() - 1; i >= 0; --i) {
            res = reducer.compose(res, dfa.transfer(cs.charAt(i)));
        }
        return res;
    }

    private static <T> TransferFunction<T> identity() {
        return new TransferFunction<T>() {
            public T next(T x) {
                return x;
            }
        };
    }
}
