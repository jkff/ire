package net.ire.rope;

import net.ire.fa.Sequence;
import net.ire.util.Function;
import net.ire.util.Reducer;

import java.util.Iterator;
import java.util.List;

import static net.ire.util.CollectionFactory.newArrayList;

/**
 * Created on: 30.08.2010 22:42:16
 */
public class RopeFactory<M> {
    private int blockSize;
    private Reducer<M> reducer;
    private Function<Character,M> map;

    public RopeFactory(int blockSize, Reducer<M> reducer, Function<Character, M> map) {
        this.blockSize = blockSize;
        this.reducer = reducer;
        this.map = map;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public Reducer<M> getReducer() {
        return reducer;
    }

    public Function<Character, M> getMap() {
        return map;
    }

    public M mapReduce(final Sequence<Character> chars) {
        return reducer.sumAll(new Sequence<M>() {
            public int length() {
                return chars.length();
            }

            public M get(int i) {
                return getMap().applyTo(chars.get(i));
            }
        });
    }
}
