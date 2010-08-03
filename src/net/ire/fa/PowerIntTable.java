package net.ire.fa;

import java.util.BitSet;

/**
 * Created on: 01.08.2010 13:23:02
 */
public class PowerIntTable implements TransferFunction<PowerIntState> {
    private BitSet[] state2next;

    public PowerIntTable(BitSet[] state2next) {
        this.state2next = state2next;
    }

    public TransferFunction<PowerIntState> followedBy(TransferFunction<PowerIntState> other) {
        int n = state2next.length;
        BitSet[] res = new BitSet[n];
        for(int state = 0; state < n; ++state) {
            BitSet ourNext = state2next[state];

            BitSet next = new BitSet(n);
            for(int bit = ourNext.nextSetBit(0); bit >= 0; bit = ourNext.nextSetBit(bit+1)) {
                next.or(((PowerIntTable)other).state2next[bit]);    
            }

            res[state] = next;
        }
        return new PowerIntTable(res);
    }

    public PowerIntState next(PowerIntState st) {
        BitSet s = st.getSubset();
        BitSet res = new BitSet(s.length());
        for(int bit = s.nextSetBit(0); bit >= 0; bit = s.nextSetBit(bit+1)) {
            res.or(state2next[bit]);
        }
        return new PowerIntState(st.getBasis(), res);
    }
}
