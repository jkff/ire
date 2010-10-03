package net.ire.util;

import net.ire.fa.Sequence;

import java.util.List;

/**
 * Contract: null must be a unit.
 *
 * Created on: 21.08.2010 20:11:35
 */
public interface Reducer<T> {
    T compose(T a, T b);

    T composeAll(Sequence<T> ts);
}
