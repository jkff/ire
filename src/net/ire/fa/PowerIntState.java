package net.ire.fa;

import net.ire.util.WrappedBitSet;

/**
 * Created on: 01.08.2010 13:20:58
 */
public class PowerIntState implements State {
    private State[] basis;
    private WrappedBitSet subset;

    public PowerIntState(State[] basis, WrappedBitSet subset) {
        this.basis = basis;
        this.subset = subset;
    }

    public State[] getBasis() {
        return basis;
    }

    public WrappedBitSet getSubset() {
        return subset;
    }

    public WrappedBitSet getTerminatedPatterns() {
        WrappedBitSet res = null;
        for(int bit = subset.nextSetBit(0); bit >= 0; bit = subset.nextSetBit(bit+1)) {
            if(res == null)
                res = (WrappedBitSet) basis[bit].getTerminatedPatterns().makeCopy();
            else
                res.or(basis[bit].getTerminatedPatterns());
        }
        return res;
    }
}
