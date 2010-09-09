package net.ire.rope;

import net.ire.DFAIndexedString;
import net.ire.DFAMatcher;
import net.ire.Match;
import net.ire.fa.*;
import net.ire.util.*;

import java.util.Collections;
import java.util.List;

import static net.ire.util.CollectionFactory.newArrayList;

/**
 * Created on: 21.08.2010 21:10:19
 */
public class RopeBasedIS<ST extends State> implements DFAIndexedString<RopeBasedIS<ST>, ST> {
    private static final int DEFAULT_BLOCK_SIZE = 128;

    private BiDFA<Character,ST> bidfa;
    private Rope<TransferFunctions<ST>> rope;

    public RopeBasedIS(BiDFA<Character,ST> bidfa, String value) {
        this(bidfa, value, DEFAULT_BLOCK_SIZE);
    }

    public RopeBasedIS(BiDFA<Character,ST> bidfa, String value, int blockSize) {
        this(bidfa, Rope.fromString(
                new RopeFactory<TransferFunctions<ST>>(
                        blockSize, new TFProduct<ST>(), new TFMap<ST>(bidfa)),
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
        return (p == null) ? null : Pair.of(
                new RopeBasedIS<ST>(bidfa, p.first),
                new RopeBasedIS<ST>(bidfa, p.second));
    }

    public <T> Pair<RopeBasedIS<ST>, RopeBasedIS<ST>> splitAfterBackRise(T seed, Function2<T, RopeBasedIS<ST>, T> addChunk, Function2<T, Character, T> addChar, Predicate<T> toBool) {
        Pair<Rope<TransferFunctions<ST>>, Rope<TransferFunctions<ST>>> p = rope.splitAfterBackRise(
                seed, toRopeAddChunkFun(addChunk), addChar, toBool);
        return (p == null) ? null : Pair.of(
                new RopeBasedIS<ST>(bidfa, p.first),
                new RopeBasedIS<ST>(bidfa, p.second));
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

    public String toString() {
        return rope.toString();
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
                    a.forward==UNIT_TF ? b.forward : b.forward == UNIT_TF ? a.forward :
                    a.forward.followedBy(b.forward),
                    a.backward==UNIT_TF ? b.backward : b.backward == UNIT_TF ? a.backward :
                    b.backward.followedBy(a.backward));
        }

        public TransferFunctions<ST> unit() {
            return (TransferFunctions<ST>) UNIT;
        }

        public TransferFunctions<ST> sumAll(final Sequence<TransferFunctions<ST>> tfs) {
            if(tfs.length() == 0) {
                return UNIT;
            }

            TransferFunction sumForward = PowerIntTable.composeAll(new Sequence<PowerIntTable>() {
                public int length() {
                    return tfs.length();
                }

                public PowerIntTable get(int i) {
                    return (PowerIntTable) tfs.get(i).forward;
                }
            });

            TransferFunction sumBackward = PowerIntTable.composeAll(new Sequence<PowerIntTable>() {
                public int length() {
                    return tfs.length();
                }

                public PowerIntTable get(int i) {
                    return (PowerIntTable) tfs.get(length()-i-1).backward;
                }
            });

            return new TransferFunctions<ST>(sumForward, sumBackward);
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
