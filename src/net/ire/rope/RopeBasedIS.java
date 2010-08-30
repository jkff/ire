package net.ire.rope;

import net.ire.DFAIndexedString;
import net.ire.DFAMatcher;
import net.ire.Match;
import net.ire.fa.BiDFA;
import net.ire.fa.State;
import net.ire.fa.TransferFunction;
import net.ire.util.*;

/**
 * Created on: 21.08.2010 21:10:19
 */
public class RopeBasedIS<ST extends State> implements DFAIndexedString<RopeBasedIS<ST>, ST> {
    private static final int DEFAULT_BLOCK_SIZE = 256;
    
    private BiDFA<Character,ST> bidfa;
    private Rope<TransferFunctions<ST>> rope;

    public RopeBasedIS(BiDFA<Character,ST> bidfa, String value) {
        this(bidfa, Rope.fromString(
                new RopeFactory<TransferFunctions<ST>>(
                        DEFAULT_BLOCK_SIZE, new TFProduct<ST>(), new TFMap<ST>(bidfa)),
                value));
    }

    private RopeBasedIS(BiDFA<Character,ST> bidfa, Rope<TransferFunctions<ST>> rope) {
        this.bidfa = bidfa;
        this.rope = rope;
    }

    public TransferFunction<ST> getForward() {
        return rope.getSum().forward;
    }

    public TransferFunction<ST> getBackward() {
        return rope.getSum().backward;
    }

    public Iterable<Match> getMatches() {
        return DFAMatcher.getMatches(bidfa, this);
    }

    public Pair<RopeBasedIS<ST>, RopeBasedIS<ST>> splitBefore(final int index) {
        Function2<Integer, Rope<TransferFunctions<ST>>, Integer> addRopeLength = new Function2<Integer, Rope<TransferFunctions<ST>>, Integer>() {
            public Integer applyTo(Integer len, Rope<TransferFunctions<ST>> rope) {
                return len + rope.length();
            }
        };
        Function2<Integer, Character, Integer> inc = new Function2<Integer, Character, Integer>() {
            public Integer applyTo(Integer len, Character c) {
                return len + 1;
            }
        };
        Predicate<Integer> isAfterIndex = new Predicate<Integer>() {
            public boolean isTrueFor(Integer x) {
                return x >= index;
            }
        };
        Pair<Rope<TransferFunctions<ST>>, Rope<TransferFunctions<ST>>> p =
                rope.splitAfterRise(0, addRopeLength, inc, isAfterIndex);
        return Pair.of(new RopeBasedIS<ST>(bidfa, p.first), new RopeBasedIS<ST>(bidfa, p.second));
    }

    public <T> Pair<RopeBasedIS<ST>, RopeBasedIS<ST>> splitAfterRise(
            T seed,
            final Function2<T, RopeBasedIS<ST>, T> addChunk, Function2<T, Character, T> addChar,
            Predicate<T> toBool)
    {
        Pair<Rope<TransferFunctions<ST>>, Rope<TransferFunctions<ST>>> p = rope.splitAfterRise(
                seed, toRopeAddChunkFun(addChunk), addChar, toBool);
        return Pair.of(new RopeBasedIS<ST>(bidfa, p.first), new RopeBasedIS<ST>(bidfa, p.second));
    }

    public <T> Pair<RopeBasedIS<ST>, RopeBasedIS<ST>> splitAfterBackRise(T seed, Function2<T, RopeBasedIS<ST>, T> addChunk, Function2<T, Character, T> addChar, Predicate<T> toBool) {
        Pair<Rope<TransferFunctions<ST>>, Rope<TransferFunctions<ST>>> p = rope.splitAfterBackRise(
                seed, toRopeAddChunkFun(addChunk), addChar, toBool);
        return Pair.of(new RopeBasedIS<ST>(bidfa, p.first), new RopeBasedIS<ST>(bidfa, p.second));
    }

    private <T> Function2<T, Rope<TransferFunctions<ST>>, T> toRopeAddChunkFun(final Function2<T, RopeBasedIS<ST>, T> addChunk) {
        return new Function2<T, Rope<TransferFunctions<ST>>, T>() {
            public T applyTo(T st, Rope<TransferFunctions<ST>> r) {
                return addChunk.applyTo(st, new RopeBasedIS<ST>(bidfa, r));
            }
        };
    }

    public RopeBasedIS<ST> append(RopeBasedIS<ST> s) {
        return new RopeBasedIS<ST>(bidfa, rope.append(s.rope));
    }

    public RopeBasedIS<ST> subSequence(int start, int end) {
        return splitBefore(start).second.splitBefore(end-start).first;
    }

    public int length() {
        return rope.length();
    }

    public char charAt(int index) {
        return rope.charAt(index);
    }

    private static class TransferFunctions<ST> {
        TransferFunction<ST> forward;
        TransferFunction<ST> backward;

        private TransferFunctions(TransferFunction<ST> forward, TransferFunction<ST> backward) {
            this.forward = forward;
            this.backward = backward;
        }
    }
    private static class TFProduct<ST> implements Reducer<TransferFunctions<ST>> {
        private static TransferFunction UNIT_TF = new TransferFunction() {
            public TransferFunction followedBy(TransferFunction other) {
                return other;
            }

            public Object next(Object x) {
                return x;
            }
        };
        private static TransferFunctions UNIT = new TransferFunctions(UNIT_TF, UNIT_TF);

        public TransferFunctions<ST> compose(TransferFunctions<ST> a, TransferFunctions<ST> b) {
            return new TransferFunctions<ST>(
                    a.forward.followedBy(b.forward),
                    b.backward.followedBy(a.backward));
        }

        public TransferFunctions<ST> unit() {
            return (TransferFunctions<ST>) UNIT;
        }
    }

    private static class TFMap<ST extends State> implements Function<Character, TransferFunctions<ST>> {
        private BiDFA<Character, ST> bidfa;

        private TFMap(BiDFA<Character, ST> bidfa) {
            this.bidfa = bidfa;
        }

        public TransferFunctions<ST> applyTo(Character c) {
            return new TransferFunctions<ST>(
                    bidfa.getForward().transfer(c),
                    bidfa.getBackward().transfer(c));
        }
    }
}
