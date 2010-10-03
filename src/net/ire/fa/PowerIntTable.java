package net.ire.fa;

import net.ire.util.Reducer;
import net.ire.util.WrappedBitSet;

import java.util.Arrays;

/**
 * Created on: 01.08.2010 13:23:02
 */
public class PowerIntTable implements TransferFunction<PowerIntState> {
    private final int numStates;
    private final int blockSize;
    private final long[] words; // numStates blocks of ceil(numStates/64) longs

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

    public static Reducer<TransferFunction<PowerIntState>> REDUCER = new Reducer<TransferFunction<PowerIntState>>() {
        public TransferFunction<PowerIntState> compose(
                TransferFunction<PowerIntState> a, TransferFunction<PowerIntState> b)
        {
            if(a == null)
                return b;
            if(b == null)
                return a;
            return ((PowerIntTable)a).followedBy((PowerIntTable) b);
        }

        public TransferFunction<PowerIntState> composeAll(Sequence<TransferFunction<PowerIntState>> ts) {
            return PowerIntTable.composeAll(ts);
        }
    };

    public PowerIntTable followedBy(PowerIntTable other) {
        long[] words = new long[this.words.length];
        long[] theirWords = other.words;
        for(int state = 0; state < numStates; ++state) {
            int ourOffset = state * blockSize;
            int bit = WrappedBitSet.nextSetBit(this.words, ourOffset, blockSize, 0);
            while (bit >= 0) {
                for (int i = 0; i < blockSize; ++i) {
                    words[ourOffset + i] |= theirWords[bit*blockSize + i];
                }
                bit = WrappedBitSet.nextSetBit(this.words, ourOffset, blockSize, bit + 1);
            }
        }
        return new PowerIntTable(numStates, words);
    }

    private static String toString(long[] ws) {
        StringBuilder sb = new StringBuilder();
        for(long w : ws) sb.append(w).append(" ");
        return sb.toString();
    }

    public PowerIntState next(PowerIntState st) {
        WrappedBitSet s = st.getSubset();
        WrappedBitSet res = new WrappedBitSet(s.numBits());
        for(int bit = s.nextSetBit(0); bit >= 0; bit = s.nextSetBit(bit+1)) {
            res.or(new WrappedBitSet(words, bit*blockSize, blockSize, numStates));
        }
        return new PowerIntState(st.getBasis(), res);
    }

    public static TransferFunction<PowerIntState> composeAll(Sequence<TransferFunction<PowerIntState>> fs) {
        PowerIntTable first = (PowerIntTable) fs.get(0);
        int numWords = first.words.length;
        long[] curWords = Arrays.copyOf(first.words, numWords);
        long[] newWords = new long[numWords];
        int numStates = first.numStates;
        int blockSize = first.blockSize;

        for (int iF = 1; iF < fs.length(); iF++) {
            for(int j = 0; j < numWords; ++j) {
                newWords[j] = 0L;
            }
            long[] nextWords = ((PowerIntTable) fs.get(iF)).words;
            for (int state = 0; state < numStates; ++state) {
                int ourOffset = state * blockSize;
                int bit = WrappedBitSet.nextSetBit(curWords, ourOffset, blockSize, 0);
                while (bit >= 0) {
                    for (int i = 0; i < blockSize; ++i) {
                        newWords[ourOffset + i] |= nextWords[bit*blockSize + i];
                    }
                    bit = WrappedBitSet.nextSetBit(curWords, ourOffset, blockSize, bit + 1);
                }
            }
            long[] tmp = curWords;
            curWords = newWords;
            newWords = tmp;
        }

        return new PowerIntTable(numStates, curWords);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int state = 0; state < numStates; ++state) {
            int offset = state * blockSize;
            sb.append(state).append(" -> ")
              .append(new WrappedBitSet(words, offset, blockSize, numStates).toString())
              .append("; ");
        }
        return sb.toString();
    }
}
