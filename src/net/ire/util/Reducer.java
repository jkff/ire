package net.ire.util;

/**
 * Created on: 21.08.2010 20:11:35
 */
public interface Reducer<T> {
    T compose(T a, T b);
    T unit();
}
