package net.ire.fa;

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

    public TransferFunction<PowerIntState> followedBy(TransferFunction<PowerIntState> other) {
        long[] words = new long[this.words.length];
        long[] theirWords = ((PowerIntTable) other).words;
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

    public PowerIntState next(PowerIntState st) {
        WrappedBitSet s = st.getSubset();
        WrappedBitSet res = new WrappedBitSet(s.length());
        for(int bit = s.nextSetBit(0); bit >= 0; bit = s.nextSetBit(bit+1)) {
            res.or(new WrappedBitSet(words, bit*blockSize, blockSize, numStates));
        }
        return new PowerIntState(st.getBasis(), res);
    }

    private static final long DEBRUIJN_64 = 0x07EDD5E59A4E28C2L;
    private static final int[] INDEX_64 = {
        63,  0, 58,  1, 59, 47, 53,  2,
        60, 39, 48, 27, 54, 33, 42,  3,
        61, 51, 37, 40, 49, 18, 28, 20,
        55, 30, 34, 11, 43, 14, 22,  4,
        62, 57, 46, 52, 38, 26, 32, 41,
        50, 36, 17, 19, 29, 10, 13, 21,
        56, 45, 25, 31, 35, 16,  9, 12,
        44, 24, 15,  8, 23,  7,  6,  5
    };

    public static PowerIntTable composeAll(Sequence<PowerIntTable> fs) {
        PowerIntTable first = fs.get(0);

        int numWords = first.words.length;
        long[] curWords = Arrays.copyOf(first.words, numWords);
        long[] newWords = new long[numWords];
        int numStates = first.numStates;
        int blockSize = first.blockSize;

        for (int iF = 1; iF < fs.length(); iF++) {
            for(int j = 0; j < numWords; ++j) {
                newWords[j] = 0L;
            }
            long[] nextWords = fs.get(iF).words;
            for (int state = 0; state < numStates; ++state) {
                int ourOffset = state * blockSize;
                for(int i = 0; i < blockSize; ++i) {
                    long w = curWords[ourOffset + i];
                    int bitBase = i * 64;
                    while(w != 0) {
                        long withoutLsb = w & (w - 1);
                        long lsb = w ^ withoutLsb;

                        int bit = bitBase + INDEX_64[((int) ((lsb * DEBRUIJN_64) >>> 58))];
                        for (int j = 0; j < blockSize; ++j) {
                            newWords[ourOffset + j] |= nextWords[bit * blockSize + j];
                        }

                        w = withoutLsb;
                    }
                }
            }
            long[] tmp = curWords;
            curWords = newWords;
            newWords = tmp;
        }

        return new PowerIntTable(numStates, curWords);
    }
}
