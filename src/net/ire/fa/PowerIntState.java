package net.ire.fa;

import java.util.BitSet;

/**
 * Created on: 01.08.2010 13:20:58
 */
public class PowerIntState implements State {
    private State[] basis;
    private BitSet subset;

    public PowerIntState(State[] basis, BitSet subset) {
        if(subset.isEmpty())
            throw new AssertionError("Unexpected: empty powerstate");
        this.basis = basis;
        this.subset = subset;
    }

    public State[] getBasis() {
        return basis;
    }

    public BitSet getSubset() {
        return subset;
    }

    public BitSet getTerminatedPatterns() {
        BitSet res = null;
        for(int bit = subset.nextSetBit(0); bit >= 0; bit = subset.nextSetBit(0)) {
            if(res == null)
                res = basis[bit].getTerminatedPatterns();
            else
                res.or(basis[bit].getTerminatedPatterns());
        }
        return res;
    }
}
