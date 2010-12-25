package org.jkff.ire.fa;

/**
 * Created on: 09.09.2010 1:08:03
 */
public interface MutableTransferFunction<T> {
    void followInPlaceBy(TransferFunction<T> other);
}
