package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.State;
import net.ire.fa.TransferFunction;

/**
 * Created on: 23.07.2010 9:23:42
 */
public class LinearIS<ST extends State> extends DFABasedIS<LinearIS<ST>,ST> {
    private CharSequence cs;

    public LinearIS(CharSequence cs, BiDFA<Character, ST> bidfa) {
        this(cs, bidfa, transferForward(bidfa, cs), transferBackward(bidfa, cs));
    }

    private LinearIS(CharSequence cs, BiDFA<Character, ST> bidfa,
                          TransferFunction<ST> forward, TransferFunction<ST> backward) {
        super(forward, backward, bidfa);
        this.cs = cs;
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

    public LinearIS<ST> reverse() {
        return new LinearIS<ST>(new StringBuilder(cs).reverse(), 
                new BiDFA<Character, ST>(bidfa.getBackward(), bidfa.getForward()));
    }

    public Pair<LinearIS<ST>, LinearIS<ST>> splitBefore(int index) {
        return Pair.of(
                new LinearIS<ST>(cs.subSequence(0, index), bidfa),
                new LinearIS<ST>(cs.subSequence(index, cs.length()), bidfa));
    }

    public Pair<LinearIS<ST>, LinearIS<ST>> splitAfterRise(Predicate<LinearIS<ST>> pred) {
        for(int i = 0; i <= length(); ++i) {
            if(pred.isTrueFor(subSequence(0, i)))
                return splitBefore(i);
        }
        return null;
    }

    public LinearIS<ST> prepend(char c) {
        return new LinearIS<ST>(c+cs.toString(), bidfa);
    }

    public LinearIS<ST> append(char c) {
        return new LinearIS<ST>(cs.toString()+c, bidfa);
    }

    public LinearIS<ST> append(LinearIS<ST> other) {
        return new LinearIS<ST>(cs.toString() + other.toString(), bidfa);
    }

    private static <ST extends State> TransferFunction<ST> transferForward(
            BiDFA<Character, ST> bidfa, CharSequence cs)
    {
        TransferFunction<ST> res = identity();
        for(int i = 0; i < cs.length(); ++i) {
            res = res.followedBy(bidfa.getForward().transfer(cs.charAt(i)));
        }
        return res;
    }

    private static <ST extends State> TransferFunction<ST> transferBackward(
            BiDFA<Character, ST> bidfa, CharSequence cs) {
        TransferFunction<ST> res = identity();
        for(int i = cs.length() - 1; i >= 0; --i) {
            res = res.followedBy(bidfa.getBackward().transfer(cs.charAt(i)));
        }
        return res;
    }

    private static <T> TransferFunction<T> identity() {
        return new TransferFunction<T>() {
            public TransferFunction<T> followedBy(TransferFunction<T> other) {
                return other;
            }

            public T next(T x) {
                return x;
            }
        };
    }
}
