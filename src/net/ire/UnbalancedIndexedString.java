package net.ire;

import net.ire.fa.DFA;
import net.ire.fa.MultiDFA;
import net.ire.fa.TransferFunction;
import org.jetbrains.annotations.Nullable;

/**
 * Created on: 25.07.2010 13:40:19
 */
public class UnbalancedIndexedString implements IndexedString<UnbalancedIndexedString> {
    private MultiDFA<Character> mdfa;
    private TransferFunction<Integer> transfer;

    private int length;

    @Nullable
    private String value;

    @Nullable
    private UnbalancedIndexedString left;
    @Nullable
    private UnbalancedIndexedString right;

    private UnbalancedIndexedString(MultiDFA<Character> mdfa, TransferFunction<Integer> transfer, int length, 
                                   String value, UnbalancedIndexedString left, UnbalancedIndexedString right)
    {
        this.mdfa = mdfa;
        this.length = length;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public Iterable<Match> getMatches() {
        // Split on predicate "term != 0".
        // First match ends at right bound of split's left part.
        // Recursively continue on split's right part.
        Predicate<UnbalancedIndexedString> hasMatch = new Predicate<UnbalancedIndexedString>() {
            public boolean isTrueFor(UnbalancedIndexedString unbalancedIndexedString) {
                int state = transfer.next(0);
                return !mdfa.getDFA().getTerminatedPatterns(state).isEmpty();
            }
        };

        throw new UnsupportedOperationException();
    }

    public Pair<UnbalancedIndexedString, UnbalancedIndexedString> splitBefore(int index) {
        if(value != null) {
            UnbalancedIndexedString leftPart = forString(value.substring(0, index));
            UnbalancedIndexedString rightPart = forString(value.substring(index));
            return Pair.of(leftPart, rightPart);
        }
        if(index <= left.length) {
            Pair<UnbalancedIndexedString, UnbalancedIndexedString> p = left.splitBefore(index);
            return Pair.of(p.first, p.second.append(right));
        } else {
            Pair<UnbalancedIndexedString, UnbalancedIndexedString> p = right.splitBefore(index - left.length);
            return Pair.of(left.append(p.first), p.second);
        }
    }

    public Pair<UnbalancedIndexedString, UnbalancedIndexedString> splitBeforeRise(final Predicate<UnbalancedIndexedString> pred) {
        if (!pred.isTrueFor(this))
            return Pair.of(this, forString(""));
        if (value != null) {
            for(int i = 0; i < value.length(); ++i) {
                UnbalancedIndexedString leftPart = forString(value.substring(0, i));
                if(pred.isTrueFor(leftPart))
                    return Pair.of(leftPart, forString(value.substring(i)));
            }
            return Pair.of(this, forString(""));
        }
        if (!pred.isTrueFor(left)) {
            Pair<UnbalancedIndexedString, UnbalancedIndexedString> p = left.splitBeforeRise(pred);
            return Pair.of(p.first, p.second.append(right));
        } else {
            Pair<UnbalancedIndexedString, UnbalancedIndexedString> p = right.splitBeforeRise(new Predicate<UnbalancedIndexedString>() {
                public boolean isTrueFor(UnbalancedIndexedString s) {
                    return pred.isTrueFor(left.append(s));
                }
            });
            return Pair.of(p.first, p.second.append(right));
        }

    }

    public UnbalancedIndexedString prepend(char c) {
        UnbalancedIndexedString left = new UnbalancedIndexedString(mdfa, mdfa.getDFA().transfer(c), 1, "" + c, null, null);
        return left.append(this);
    }

    public UnbalancedIndexedString append(char c) {
        UnbalancedIndexedString right = new UnbalancedIndexedString(mdfa, mdfa.getDFA().transfer(c), 1, "" + c, null, null);
        return this.append(right);
    }

    public UnbalancedIndexedString append(UnbalancedIndexedString other) {
        return new UnbalancedIndexedString(mdfa, this.transfer.followedBy(other.transfer), length + other.length, null, this, other);
    }

    public int length() {
        return length;
    }

    public char charAt(int index) {
        if(value != null)
            return value.charAt(index);
        if(index <= left.length)
            return left.charAt(index);
        return right.charAt(index - left.length);
    }

    public UnbalancedIndexedString subSequence(int start, int end) {
        if(value != null)
            return forString(value.substring(start, end));
        if(end <= left.length)
            return left.subSequence(start, end);
        if(start > left.length)
            return right.subSequence(start - left.length, end - left.length);
        return left.subSequence(start, left.length).append(right.subSequence(0, end - left.length));
    }

    private static TransferFunction<Integer> transfer(DFA<Character> dfa, String s) {
        TransferFunction<Integer> res = new TransferFunction<Integer>() {
            public TransferFunction<Integer> followedBy(TransferFunction<Integer> other) {
                return other;
            }

            public Integer next(Integer x) {
                return x;
            }
        };

        for(char c : s.toCharArray())
            res = res.followedBy(dfa.transfer(c));

        return res;
    }

    private UnbalancedIndexedString forString(String s) {
        TransferFunction<Integer> transfer = transfer(mdfa.getDFA(), s);
        return new UnbalancedIndexedString(mdfa, transfer, s.length(), s, null, null);
    }

}
