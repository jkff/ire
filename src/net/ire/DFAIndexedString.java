package net.ire;

import net.ire.fa.TransferFunction;

/**
 * Created on: 21.08.2010 21:03:13
 */
public interface DFAIndexedString<S extends DFAIndexedString<S,ST>, ST> extends IndexedString<S> {
    TransferFunction<ST> getForward();
    TransferFunction<ST> getBackward();
}
