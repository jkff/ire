package net.ire.monoid;

/**
 * Created on: 09.09.2010 21:11:16
 */
public interface Monoid<T> {
    T unit();

    T reduce(T a, T b);

    boolean doCommute(T a, T b);

}
