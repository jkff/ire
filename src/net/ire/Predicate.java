package net.ire;

/**
 * Created on: 25.07.2010 14:10:21
 */
public interface Predicate<T> {
    boolean isTrueFor(T t);
}
