package net.ire.fa;

import net.ire.util.WrappedBitSet;

/**
 * Created on: 31.07.2010 15:16:46
 */
public class IntState implements State {
    private int index;
    private WrappedBitSet terminatedPatterns;

    public IntState(int index, WrappedBitSet terminatedPatterns) {
        this.index = index;
        this.terminatedPatterns = terminatedPatterns;
    }

    public int getIndex() {
        return index;
    }

    public WrappedBitSet getTerminatedPatterns() {
        return terminatedPatterns;
    }
}
