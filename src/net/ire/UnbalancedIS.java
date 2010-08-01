package net.ire;

import net.ire.fa.*;
import org.jetbrains.annotations.Nullable;

/**
 * Unbalanced indexed string
 *
 * Created on: 25.07.2010 13:40:19
 */
public class UnbalancedIS<ST extends State> extends DFABasedIS<UnbalancedIS<ST>, ST> {
    private int length;

    private CharSequence value;

    private UnbalancedIS<ST> left;
    private UnbalancedIS<ST> right;

    private UnbalancedIS(
            BiDFA<Character,ST> bidfa,
            TransferFunction<ST> forward, TransferFunction<ST> backward,
            int length, CharSequence value, UnbalancedIS left, UnbalancedIS right)
    {
        super(forward, backward, bidfa);
        this.length = length;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public Pair<UnbalancedIS<ST>, UnbalancedIS<ST>> splitBefore(int index) {
        if(value != null) {
            UnbalancedIS<ST> leftPart = forString(value.subSequence(0, index));
            UnbalancedIS<ST> rightPart = forString(value.subSequence(index, value.length()));
            return Pair.of(leftPart, rightPart);
        }
        if(index <= left.length) {
            Pair<UnbalancedIS<ST>, UnbalancedIS<ST>> p = left.splitBefore(index);
            return Pair.of(p.first, p.second.append(right));
        } else {
            Pair<UnbalancedIS<ST>, UnbalancedIS<ST>> p = right.splitBefore(index - left.length);
            return Pair.of(left.append(p.first), p.second);
        }
    }

    @Nullable
    public Pair<UnbalancedIS<ST>, UnbalancedIS<ST>> splitAfterRise(final Predicate<UnbalancedIS<ST>> pred) {
        if (!pred.isTrueFor(this))
            return null;
        if (value != null) {
            for(int i = 0; i < value.length(); ++i) {
                UnbalancedIS<ST> leftPart = forString(value.subSequence(0, i));
                if(pred.isTrueFor(leftPart))
                    return Pair.of(leftPart, forString(value.subSequence(i, value.length())));
            }
            return Pair.of(this, forString(""));
        }
        if (pred.isTrueFor(left)) {
            Pair<UnbalancedIS<ST>, UnbalancedIS<ST>> p = left.splitAfterRise(pred);
            return Pair.of(p.first, p.second.append(right));
        } else {
            Pair<UnbalancedIS<ST>, UnbalancedIS<ST>> p = right.splitAfterRise(new Predicate<UnbalancedIS<ST>>() {
                public boolean isTrueFor(UnbalancedIS<ST> s) {
                    return pred.isTrueFor(left.append(s));
                }
            });
            return Pair.of(p.first, p.second.append(right));
        }
    }

    public UnbalancedIS<ST> prepend(char c) {
        return forChar(bidfa, c).append(this);
    }

    public UnbalancedIS<ST> append(UnbalancedIS<ST> other) {
        return new UnbalancedIS<ST>(
                bidfa,
                this.forward.followedBy(other.forward),
                other.backward.followedBy(this.backward),
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

    public UnbalancedIS<ST> subSequence(int start, int end) {
        if(value != null)
            return forString(value.subSequence(start, end));
        if(end <= left.length)
            return left.subSequence(start, end);
        if(start > left.length)
            return right.subSequence(start - left.length, end - left.length);
        return left.subSequence(start, left.length).append(right.subSequence(0, end - left.length));
    }

    public UnbalancedIS<ST> reverse() {
        if(value == null) {
            return forString(new StringBuilder(value).reverse().toString());
        } else {
            return new UnbalancedIS<ST>(bidfa, backward, forward, length, null, right, left);
        }
    }

    private static <ST extends State> TransferFunction<ST> transfer(
            DFA<Character,ST> dfa, CharSequence s)
    {
        TransferFunction<ST> res = new TransferFunction<ST>() {
            public TransferFunction<ST> followedBy(TransferFunction<ST> other) {
                return other;
            }

            public ST next(ST x) {
                return x;
            }
        };

        for(int i = 0; i < s.length(); ++i)
            res = res.followedBy(dfa.transfer(s.charAt(i)));

        return res;
    }

    public UnbalancedIS<ST> append(char c) {
        return this.append(forChar(bidfa, c));
    }

    private static <ST extends State> UnbalancedIS<ST> forChar(BiDFA<Character, ST> bidfa, char c) {
        return new UnbalancedIS<ST>(bidfa, bidfa.getForward().transfer(c), bidfa.getBackward().transfer(c), 1, ""+c, null, null);
    }

    private UnbalancedIS<ST> forString(CharSequence s) {
        CharSequence rs = new StringBuilder(s).reverse();
        TransferFunction<ST> forward = transfer(bidfa.getForward(), s);
        TransferFunction<ST> backward = transfer(bidfa.getBackward(), rs);
        return new UnbalancedIS<ST>(bidfa, forward, backward, s.length(), s, null, null);
    }
}
