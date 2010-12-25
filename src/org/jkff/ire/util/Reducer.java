package org.jkff.ire.util;

import org.jkff.ire.fa.Sequence;

/**
 * Contract: null must be a unit.
 *
 * Created on: 21.08.2010 20:11:35
 */
public interface Reducer<T> {
    T compose(T a, T b);

    T composeAll(Sequence<T> ts);
}
