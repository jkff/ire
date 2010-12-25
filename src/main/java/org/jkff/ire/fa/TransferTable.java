package org.jkff.ire.fa;

/**
 * Created on: 31.07.2010 15:18:23
 */
public interface TransferTable<C,S> {
    TransferFunction<S> forToken(C token);
}
