package net.ire.rope;

import net.ire.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created on: 21.08.2010 17:46:38
 */
public class Rope<M> {
    @NotNull
    private RopeFactory<M> factory;

    private final M sum;

    private final Rope<M> a,b,c;
    private final int h;
    private final int length;

    private final String block;

    private Rope(Rope<M> a, Rope<M> b, M sum) {
        this(a, b, null, a.factory, null, sum);
    }

    private Rope(Rope<M> a, Rope<M> b, Rope<M> c, M sum) {
        this(a, b, c, a.factory, null, sum);
    }

    private Rope(RopeFactory<M> factory, String block, M sum) {
        this(null, null, null, factory, block, sum);
    }

    private Rope(RopeFactory<M> factory, String block) {
        this(null, null, null, factory, block, sumString(factory, block));
    }

    private static <M> M sumString(RopeFactory<M> factory, String block) {
        Function<Character, M> map = factory.getMap();
        Reducer<M> reducer = factory.getReducer();
        M sum = reducer.unit();
        for(int i = 0; i < block.length(); ++i) {
            sum = reducer.compose(sum, map.applyTo(block.charAt(i)));
        }
        return sum;
    }

    private Rope(Rope<M> a, Rope<M> b, Rope<M> c, RopeFactory<M> factory, String block, M sum) {
        if(block != null) {
            assert a == null : "Block can't have a child: 'a'";
            assert b == null : "Block can't have a child: 'b'";
            assert c == null : "Block can't have a child: 'c'";
            this.a = this.b = this.c = null;
            this.h = 0;
            this.length = block.length();
            this.block = block;
            this.factory = factory;
            this.sum = sum;
        } else {
            assert a != null : "Fork must have a child: 'a'";
            assert b != null : "Fork must have a child: 'b'";
            assert a.h == b.h : "Fork's 'a' and 'b' children must have same height";
            this.a = a;
            this.b = b;
            this.block = null;
            this.factory = factory;
            this.sum = sum;
            if(c == null) {
                this.c = null;
                this.h = a.h + 1;
                this.length = a.length + b.length;
            } else {
                assert a.h == c.h : "Fork's 'a' and 'c' children must have same height";
                this.c = c;
                this.h = a.h + 1;
                this.length = a.length + b.length + c.length;
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
        int blockSize = left.factory.getBlockSize();
        Reducer<M> reducer = left.factory.getReducer();

        M sum = reducer.compose(left.sum, right.sum);
        
        if(left.h == right.h) {
            if(left.h > 0)
                return new Rope<M>(left, right, sum);
            if(!left.isUnderflownBlock() && !right.isUnderflownBlock())
                return new Rope<M>(left, right, sum);
            String bigBlock = left.block + right.block;
            if(bigBlock.length() <= 2 * blockSize - 1)
                return new Rope<M>(left.factory, bigBlock, sum);
            return new Rope<M>(
                    new Rope<M>(left.factory, bigBlock.substring(0, blockSize)),
                    new Rope<M>(left.factory, bigBlock.substring(blockSize, bigBlock.length())),
                    sum);
        } else if(left.h == right.h + 1) {
            if(left.c == null)
                return new Rope<M>(left.a, left.b, right, sum);
            else
                return new Rope<M>(
                        // Optimization opportunity: remember a+b and b+c sums in 3-child nodes
                        new Rope<M>(left.a, left.b, reducer.compose(left.a.sum, left.b.sum)),
                        new Rope<M>(left.c, right, reducer.compose(left.c.sum, right.sum)),
                        sum);
        } else if(right.h == left.h + 1) {
            if(right.c == null)
                return new Rope<M>(left, right.a, right.b, sum);
            else
                return new Rope<M>(
                        new Rope<M>(left, right.a, reducer.compose(left.sum, right.a.sum)),
                        // Optimization opportunity: remember a+b and b+c sums in 3-child nodes
                        new Rope<M>(right.b, right.c, reducer.compose(right.b.sum, right.c.sum)),
                        sum);
        } else if (left.h > right.h + 1) {
            if(left.c == null)
                // This would not be well-founded recursion, if not for the two previous cases
                // left.b.append(right) may be at most left.a.h+1 high and this will be handled by them.
                return left.a.append(left.b.append(right));
            else
                // etc.
                return (left.a.append(left.b)).append(left.c.append(right));
        } else { // right.h > left.h + 1
            if(right.c == null)
                return left.append(right.a).append(right.b);
            else
                return (left.append(right.a)).append(right.b.append(right.c));
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
                            new Rope<M>(this.factory, block.substring(0,i)),
                            new Rope<M>(this.factory, block.substring(i, block.length())));
                s = addChar.applyTo(s, block.charAt(i));
            }
            if(toBool.isTrueFor(s))
                return Pair.of(this, new Rope<M>(this.factory, ""));
            return null;
        } else {
            if(toBool.isTrueFor(seed))
                return Pair.of(new Rope<M>(this.factory, ""), this);
            S afterA = addChunk.applyTo(seed, a);
            if(toBool.isTrueFor(afterA)) {
                Pair<Rope<M>,Rope<M>> sa = a.splitAfterRise(seed, addChunk, addChar, toBool);
                return (c == null)
                        ? Pair.of(sa.first, sa.second.append(b))
                        : Pair.of(sa.first, sa.second.append(b).append(c));
            }
            S afterB = addChunk.applyTo(afterA, b);
            if(toBool.isTrueFor(afterB)) {
                Pair<Rope<M>,Rope<M>> sb = b.splitAfterRise(afterA, addChunk, addChar, toBool);
                return (c == null)
                        ? Pair.of(a.append(sb.first), sb.second)
                        : Pair.of(a.append(sb.first), sb.second.append(c));
            }
            if(c == null)
                return null;
            S afterC = addChunk.applyTo(afterB, c);
            if(toBool.isTrueFor(afterC)) {
                Pair<Rope<M>,Rope<M>> sc = c.splitAfterRise(afterB, addChunk, addChar, toBool);
                return Pair.of(a.append(b).append(sc.first), sc.second);
            }
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
                            new Rope<M>(this.factory, block.substring(0,i+1)),
                            new Rope<M>(this.factory, block.substring(i+1, block.length())));
                s = addChar.applyTo(s, block.charAt(i));
            }
            if(toBool.isTrueFor(s))
                return Pair.of(new Rope<M>(this.factory, ""), this);
            return null;
        } else {
            if(toBool.isTrueFor(seed))
                return Pair.of(this, new Rope<M>(this.factory, ""));
            S beforeC = seed;
            if(c != null) {
                beforeC = addChunk.applyTo(seed, c);
                if(toBool.isTrueFor(beforeC)) {
                    Pair<Rope<M>,Rope<M>> sc = c.splitAfterBackRise(seed, addChunk, addChar, toBool);
                    return Pair.of(a.append(b).append(sc.first), sc.second);
                }
            }
            S beforeB = addChunk.applyTo(beforeC, b);
            if(toBool.isTrueFor(beforeB)) {
                Pair<Rope<M>,Rope<M>> sb = b.splitAfterBackRise(beforeC, addChunk, addChar, toBool);
                return (c == null)
                        ? Pair.of(a.append(sb.first), sb.second)
                        : Pair.of(a.append(sb.first), sb.second.append(c));
            }
            S beforeA = addChunk.applyTo(beforeB, a);
            if(toBool.isTrueFor(beforeA)) {
                Pair<Rope<M>,Rope<M>> sa = a.splitAfterBackRise(beforeB, addChunk, addChar, toBool);
                return (c == null)
                        ? Pair.of(sa.first, sa.second.append(b))
                        : Pair.of(sa.first, sa.second.append(b).append(c));
            }
            return null;
        }
    }

    private boolean isUnderflownBlock() {
        return h==0 && block.length() < factory.getBlockSize();
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

    public static <M> Rope<M> fromString(RopeFactory<M> factory, String value) {
        Rope<M> res = new Rope<M>(factory, "");
        for(int i = 0; i < value.length(); i += 2 * factory.getBlockSize() - 1) {
            String block = value.substring(i, Math.min(value.length(), i + 2 * factory.getBlockSize() - 1));
            res = res.append(new Rope<M>(factory, block));
        }
        return res;
    }
}
