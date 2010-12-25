package org.jkff.ire.fa;

/**
 * Created on: 22.07.2010 23:48:22
 */
public interface TransferFunction<T> {
    T next(T t);
}
