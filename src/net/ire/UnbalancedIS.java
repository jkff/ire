package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.DFA;
import net.ire.fa.TransferFunction;
import org.jetbrains.annotations.Nullable;

/**
 * Unbalanced indexed string
 *
 * Created on: 25.07.2010 13:40:19
 */
public class UnbalancedIS extends DFABasedIS<UnbalancedIS> {

    private int length;

    @Nullable
    private CharSequence value;

    @Nullable
    private UnbalancedIS left;
    @Nullable
    private UnbalancedIS right;

    private UnbalancedIS(
            BiDFA<Character> bidfa,
            TransferFunction<Integer> forward, TransferFunction<Integer> backward,
            int length, CharSequence value, UnbalancedIS left, UnbalancedIS right)
    {
        super(forward, backward, bidfa);
        this.length = length;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public Pair<UnbalancedIS, UnbalancedIS> splitBefore(int index) {
        if(value != null) {
            UnbalancedIS leftPart = forString(value.subSequence(0, index));
            UnbalancedIS rightPart = forString(value.subSequence(index, value.length()));
            return Pair.of(leftPart, rightPart);
        }
        if(index <= left.length) {
            Pair<UnbalancedIS, UnbalancedIS> p = left.splitBefore(index);
            return Pair.of(p.first, p.second.append(right));
        } else {
            Pair<UnbalancedIS, UnbalancedIS> p = right.splitBefore(index - left.length);
            return Pair.of(left.append(p.first), p.second);
        }
    }

    @Nullable
    public Pair<UnbalancedIS, UnbalancedIS> splitAfterRise(final Predicate<UnbalancedIS> pred) {
        if (!pred.isTrueFor(this))
            return null;
        if (value != null) {
            for(int i = 0; i < value.length(); ++i) {
                UnbalancedIS leftPart = forString(value.subSequence(0, i));
                if(pred.isTrueFor(leftPart))
                    return Pair.of(leftPart, forString(value.subSequence(i, value.length())));
            }
            return Pair.of(this, forString(""));
        }
        if (pred.isTrueFor(left)) {
            Pair<UnbalancedIS, UnbalancedIS> p = left.splitAfterRise(pred);
            return Pair.of(p.first, p.second.append(right));
        } else {
            Pair<UnbalancedIS, UnbalancedIS> p = right.splitAfterRise(new Predicate<UnbalancedIS>() {
                public boolean isTrueFor(UnbalancedIS s) {
                    return pred.isTrueFor(left.append(s));
                }
            });
            return Pair.of(p.first, p.second.append(right));
        }
    }

    public UnbalancedIS prepend(char c) {
        UnbalancedIS left = new UnbalancedIS(
                bidfa,
                bidfa.getForward().transfer(c), bidfa.getBackward().transfer(c),
                1, "" + c, null, null);
        return left.append(this);
    }

    public UnbalancedIS append(char c) {
        UnbalancedIS right = new UnbalancedIS(
                bidfa,
                bidfa.getForward().transfer(c), bidfa.getBackward().transfer(c),
                1, "" + c, null, null);
        return this.append(right);
    }

    public UnbalancedIS append(UnbalancedIS other) {
        return new UnbalancedIS(
                bidfa,
                this.forward.followedBy(other.forward), other.backward.followedBy(this.backward),
                length + other.length, null, this, other);
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

    public UnbalancedIS subSequence(int start, int end) {
        if(value != null)
            return forString(value.subSequence(start, end));
        if(end <= left.length)
            return left.subSequence(start, end);
        if(start > left.length)
            return right.subSequence(start - left.length, end - left.length);
        return left.subSequence(start, left.length).append(right.subSequence(0, end - left.length));
    }

    public IndexedString<UnbalancedIS> reverse() {
        if(value == null) {
            return forString(new StringBuilder(value).reverse().toString());
        } else {
            return new UnbalancedIS(bidfa, backward, forward, length, null, right, left);
        }
    }

    public boolean traverseWith(CharIteratee i) {
        if(value == null) {
            return left.traverseWith(i) && right.traverseWith(i);
        } else {
            for(int j = 0; j < value.length(); ++j) {
                if(!i.onNext(value.charAt(j)))
                    return false;
            }
            return true;
        }
    }

    private static TransferFunction<Integer> transfer(DFA<Character> dfa, CharSequence s) {
        TransferFunction<Integer> res = new TransferFunction<Integer>() {
            public TransferFunction<Integer> followedBy(TransferFunction<Integer> other) {
                return other;
            }

            public Integer next(Integer x) {
                return x;
            }
        };

        for(int i = 0; i < s.length(); ++i)
            res = res.followedBy(dfa.transfer(s.charAt(i)));

        return res;
    }

    private UnbalancedIS forString(CharSequence s) {
        CharSequence rs = new StringBuilder(s).reverse();
        TransferFunction<Integer> forward = transfer(bidfa.getForward(), s);
        TransferFunction<Integer> backward = transfer(bidfa.getBackward(), rs);
        return new UnbalancedIS(bidfa, forward, backward, s.length(), s, null, null);
    }

}
