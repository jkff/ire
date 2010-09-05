package net.ire.fa;

import net.ire.util.WrappedBitSet;

/**
 * Created on: 01.08.2010 13:23:02
 */
public class PowerIntTable implements TransferFunction<PowerIntState> {
    private final int numStates;
    private final int blockSize;
    private final long[] words; // numStates blocks of ceil(numStates/32) longs

    public PowerIntTable(WrappedBitSet[] state2next) {
        this.numStates = state2next.length;
        this.blockSize = (63+numStates) / 64;
        this.words = new long[numStates * blockSize];
        for(int s = 0; s < numStates; ++s) {
            new WrappedBitSet(words, s*blockSize, blockSize, numStates).or(state2next[s]);
        }
    }

    private PowerIntTable(int numStates, long[] words) {
        this.numStates = numStates;
        this.blockSize = (63+numStates) / 64;
        this.words = words;
    }

    public TransferFunction<PowerIntState> followedBy(TransferFunction<PowerIntState> other) {
        long[] words = new long[this.words.length];
        for(int state = 0; state < numStates; ++state) {
            WrappedBitSet ourNext = new WrappedBitSet(this.words, state*blockSize, blockSize, numStates);

            WrappedBitSet next = new WrappedBitSet(words, state*blockSize, blockSize, numStates);
            for(int bit = ourNext.nextSetBit(0); bit >= 0; bit = ourNext.nextSetBit(bit+1)) {
                next.or(new WrappedBitSet(((PowerIntTable) other).words, bit*blockSize, blockSize, numStates));
            }
        }
        return new PowerIntTable(numStates, words);
    }

    public PowerIntState next(PowerIntState st) {
        WrappedBitSet s = st.getSubset();
        WrappedBitSet res = new WrappedBitSet(s.length());
        for(int bit = s.nextSetBit(0); bit >= 0; bit = s.nextSetBit(bit+1)) {
            res.or(new WrappedBitSet(words, bit*blockSize, blockSize, numStates));
        }
        return new PowerIntState(st.getBasis(), res);
    }
}
