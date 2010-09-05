package net.ire.fa;

import net.ire.util.WrappedBitSet;

/**
 * Created on: 31.07.2010 14:57:34
 */
public interface State {
    WrappedBitSet getTerminatedPatterns();
}
