package net.ire.util;

import net.ire.fa.Sequence;

import java.util.List;

/**
 * Created on: 21.08.2010 20:11:35
 */
public interface Reducer<T> {
    T compose(T a, T b);
    T unit();
    T sumAll(Sequence<T> ts);
}
