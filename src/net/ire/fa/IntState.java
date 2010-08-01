package net.ire.fa;

import java.util.BitSet;

/**
 * Created on: 31.07.2010 15:16:46
 */
public class IntState implements State {
    private int index;
    private BitSet terminatedPatterns;

    public IntState(int index, BitSet terminatedPatterns) {
        this.index = index;
        this.terminatedPatterns = terminatedPatterns;
    }

    public int getIndex() {
        return index;
    }

    public BitSet getTerminatedPatterns() {
        return terminatedPatterns;
    }
}
