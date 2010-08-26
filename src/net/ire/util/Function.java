package net.ire.util;

/**
 * Created on: 21.08.2010 20:13:50
 */
public interface Function<A, B> {
    B applyTo(A a);
}
