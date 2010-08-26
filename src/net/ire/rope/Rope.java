package net.ire.rope;

import net.ire.util.*;

/**
 * Created on: 21.08.2010 17:46:38
 */
public class Rope<M> {
    private static final int BLOCK_N = 128;

    private final Reducer<M> reducer;
    private final Function<Character, M> map;

    private final M sum;

    private final Rope<M> a,b,c;
    private final int h;
    private final int length;

    private final String block;

    private Rope(Rope<M> a, Rope<M> b) {
        this(a, b, null, null, null, null);
    }

    private Rope(Rope<M> a, Rope<M> b, Rope<M> c) {
        this(a, b, c, null, null, null);
    }

    private Rope(Reducer<M> reducer, Function<Character, M> map, String block) {
        this(null, null, null, reducer, map, block);
    }

    private Rope(Rope<M> a, Rope<M> b, Rope<M> c, Reducer<M> reducer, Function<Character, M> map, String block) {
        if(block != null) {
            assert a == null : "Block can't have a child: 'a'";
            assert b == null : "Block can't have a child: 'b'";
            assert c == null : "Block can't have a child: 'c'";
            this.a = this.b = this.c = null;
            this.h = 0;
            this.length = block.length();
            this.block = block;
            this.reducer = reducer;
            this.map = map;
            M sum = reducer.unit();
            for(int i = 0; i < block.length(); ++i) {
                sum = reducer.compose(sum, map.applyTo(block.charAt(i)));
            }
            this.sum = sum;
        } else {
            assert a != null : "Fork must have a child: 'a'";
            assert b != null : "Fork must have a child: 'b'";
            assert a.h == b.h : "Fork's 'a' and 'b' children must have same height";
            this.a = a;
            this.b = b;
            this.block = null;
            this.reducer = a.reducer;
            this.map = a.map;
            if(c == null) {
                this.c = null;
                this.h = a.h + 1;
                this.length = a.length + b.length;
                this.sum = reducer.compose(a.sum, b.sum);
            } else {
                assert a.h == c.h : "Fork's 'a' and 'c' children must have same height";
                this.c = c;
                this.h = a.h + 1;
                this.length = a.length + b.length + c.length;
                this.sum = reducer.compose(a.sum, reducer.compose(b.sum, c.sum));
            }
        }
    }

    public int length() {
        return length;
    }

    public char charAt(int index) {
        if(block != null)
            return block.charAt(index);
        if(index < a.length())
            return a.charAt(index);
        if(index < a.length() + b.length())
            return b.charAt(index - a.length());
        return c.charAt(index - a.length() - b.length());
    }

    public M getSum() {
        return sum;
    }

    public Rope<M> append(Rope<M> other) {
        return append(this, other);
    }

    private static <M> Rope<M> append(Rope<M> left, Rope<M> right) {
        if(left.h == right.h) {
            if(left.h > 0)
                return new Rope<M>(left, right);
            if(!left.isUnderflownBlock() && !right.isUnderflownBlock())
                return new Rope<M>(left, right);
            String bigBlock = left.block + right.block;
            if(bigBlock.length() <= 2 * BLOCK_N - 1)
                return new Rope<M>(left.reducer, left.map, bigBlock);
            return new Rope(
                    new Rope<M>(left.reducer, left.map, bigBlock.substring(0, BLOCK_N)),
                    new Rope<M>(left.reducer, left.map, bigBlock.substring(BLOCK_N, bigBlock.length())));
        } else if(left.h == right.h + 1) {
            if(left.c == null)
                return new Rope<M>(left.a, left.b, right);
            else
                return new Rope<M>(new Rope<M>(left.a, left.b), new Rope<M>(left.c, right));
        } else if(right.h == left.h + 1) {
            if(right.c == null)
                return new Rope<M>(left, right.a, right.b);
            else
                return new Rope<M>(new Rope<M>(left, right.a), new Rope<M>(right.b, right.c));
        } else if (left.h > right.h + 1) {
            if(left.c == null)
                // This would not be well-founded recursion, if not for the two previous cases
                // left.b.append(right) may be at most left.a.h+1 high and this will be handled by them.
                return left.a.append(left.b.append(right));
            else
                // etc.
                return left.a.append(left.b.append(left.c.append(right)));
        } else { // right.h > left.h + 1
            if(right.c == null)
                return left.append(right.a).append(right.b);
            else
                return left.append(right.a).append(right.b).append(right.c);
        }
    }

    public <S> Pair<Rope<M>,Rope<M>> splitAfterRise(
            S seed,
            Function2<S,Rope<M>,S> addChunk, Function2<S,Character,S> addChar,
            Predicate<S> toBool)
    {
        if(block != null) {
            S s = seed;
            for(int i = 0; i < block.length() ; ++i) {
                if(toBool.isTrueFor(s))
                    return Pair.of(
                            new Rope<M>(this.reducer, this.map, block.substring(0,i)),
                            new Rope<M>(this.reducer, this.map, block.substring(i, block.length())));
                s = addChar.applyTo(s, block.charAt(i));
            }
            return null;
        } else {
            if(toBool.isTrueFor(seed))
                return Pair.of(new Rope<M>(this.reducer, this.map, ""), this);
            S afterA = addChunk.applyTo(seed, a);
            if(toBool.isTrueFor(afterA)) {
                Pair<Rope<M>,Rope<M>> sa = a.splitAfterRise(seed, addChunk, addChar, toBool);
                return (c == null)
                        ? Pair.of(sa.first, sa.second.append(b))
                        : Pair.of(sa.first, sa.second.append(b).append(c));
            }
            S afterB = addChunk.applyTo(afterA, b);
            if(toBool.isTrueFor(afterB)) {
                Pair<Rope<M>,Rope<M>> sb = a.splitAfterRise(afterA, addChunk, addChar, toBool);
                return (c == null) ? Pair.of(this, new Rope<M>(this.reducer, this.map, "")) : Pair.of(this, c);
            }
            if(c == null)
                return null;
            s = addChunk.applyTo(s, c);
            if(toBool.isTrueFor(s))
                return Pair.of(this, new Rope<M>(this.reducer, this.map, ""));
            return null;
        }
    }
    public <S> Pair<Rope<M>,Rope<M>> splitAfterBackRise(
            S seed,
            Function2<S, Rope<M>, S> addChunk, Function2<S, Character, S> addChar,
            Predicate<S> toBool)
    {
        if(block != null) {
            S s = seed;
            for(int i = block.length() - 1; i >= 0; --i) {
                if(toBool.isTrueFor(s))
                    return Pair.of(
                            new Rope<M>(this.reducer, this.map, block.substring(0,i+1)),
                            new Rope<M>(this.reducer, this.map, block.substring(i+1, block.length())));
                s = addChar.applyTo(s, block.charAt(i));
            }
            return null;
        } else {
            S s = seed;
            if(toBool.isTrueFor(s))
                return Pair.of(this, new Rope<M>(this.reducer, this.map, ""));
            if(c != null) {
                s = addChunk.applyTo(s, c);
                if(toBool.isTrueFor(s)) {
                    Split c
                }
            }
            s = addChunk.applyTo(s, b);
            if(toBool.isTrueFor(s))
                return (c == null) ? Pair.of(this, new Rope<M>(this.reducer, this.map, "")) : Pair.of(this, c);
            if(c == null)
                return null;
            s = addChunk.applyTo(s, c);
            if(toBool.isTrueFor(s))
                return Pair.of(this, new Rope<M>(this.reducer, this.map, ""));
            return null;
        }
    }

    private boolean isUnderflownBlock() {
        return h==0 && block.length() < BLOCK_N;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        return toString(res).toString();
    }

    private StringBuilder toString(StringBuilder sb) {
        if(block != null) {
            sb.append(block);
        } else {
            a.toString(sb);
            b.toString(sb);
            if(c != null)
                c.toString(sb);
        }
        return sb;
    }

    public static <M> Rope<M> fromString(Reducer<M> reducer, Function<Character,M> map, String value) {
        Rope<M> res = new Rope<M>(reducer, map, "");
        for(int i = 0; i < value.length(); i += 2 * BLOCK_N - 1) {
            res = res.append(new Rope<M>(reducer, map, value.substring(i, i + 2 * BLOCK_N - 1)));
        }
        return res;
    }
}
