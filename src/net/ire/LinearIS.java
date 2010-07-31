package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.TransferFunction;

/**
 * Created on: 23.07.2010 9:23:42
 */
public class LinearIS extends DFABasedIS<LinearIS> {
    private CharSequence cs;

    public LinearIS(CharSequence cs, BiDFA<Character> bidfa) {
        this(cs, bidfa, transferForward(bidfa, cs), transferBackward(bidfa, cs));
    }

    private LinearIS(CharSequence cs, BiDFA<Character> bidfa, TransferFunction<Integer> forward, TransferFunction<Integer> backward) {
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

    public LinearIS subSequence(int start, int end) {
        return new LinearIS(cs.subSequence(start, end), bidfa);
    }

    public IndexedString<LinearIS> reverse() {
        return new LinearIS(new StringBuilder(cs).reverse(), new BiDFA<Character>(bidfa.getBackward(), bidfa.getForward()));
    }

    public boolean traverseWith(CharIteratee i) {
        for(int j = 0; j < cs.length(); ++j) {
            if(!i.onNext(cs.charAt(j)))
                return false;
        }
        return true;
    }

    public Pair<LinearIS, LinearIS> splitBefore(int index) {
        return Pair.of(new LinearIS(cs.subSequence(0, index), bidfa), new LinearIS(cs.subSequence(index, cs.length()), bidfa));
    }

    public Pair<LinearIS, LinearIS> splitAfterRise(Predicate<LinearIS> pred) {
        for(int i = 0; i <= length(); ++i) {
            if(pred.isTrueFor(subSequence(0, i)))
                return splitBefore(i);
        }
        return null;
    }

    public LinearIS prepend(char c) {
        return new LinearIS(c+cs.toString(), bidfa);
    }

    public LinearIS append(char c) {
        return new LinearIS(cs.toString()+c, bidfa);
    }

    public LinearIS append(LinearIS other) {
        return new LinearIS(cs.toString() + other.toString(), bidfa);
    }

    private static TransferFunction<Integer> transferForward(BiDFA<Character> bidfa, CharSequence cs) {
        TransferFunction<Integer> res = identity();
        for(int i = 0; i < cs.length(); ++i) {
            res = res.followedBy(bidfa.getForward().transfer(cs.charAt(i)));
        }
        return res;
    }

    private static TransferFunction<Integer> transferBackward(BiDFA<Character> bidfa, CharSequence cs) {
        TransferFunction<Integer> res = identity();
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
