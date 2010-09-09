package net.ire.fa;

/**
 * Created on: 09.09.2010 2:13:48
 */
public interface Sequence<T> {
    int length();
    T get(int i);
}
