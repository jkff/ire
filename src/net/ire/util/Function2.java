package net.ire.util;

/**
 * Created on: 21.08.2010 18:39:24
 */
public interface Function2<A,B,C> {
    C applyTo(A a, B b);
}
