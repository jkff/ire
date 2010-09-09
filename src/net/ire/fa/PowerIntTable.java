package net.ire.fa;

import net.ire.util.Function2;
import net.ire.util.WrappedBitSet;

import java.util.Arrays;

/**
 * Created on: 01.08.2010 13:23:02
 */
public class PowerIntTable implements TransferFunction<PowerIntState> {
    private final int numStates;
    private final int blockSize;
    private final long[] words; // numStates blocks of ceil(numStates/64) longs

    // TODO get rid of this hack
    private final Function2<PowerIntTable, PowerIntTable, Boolean> doCommute;

    public PowerIntTable(WrappedBitSet[] state2next, Function2<PowerIntTable, PowerIntTable, Boolean> doCommute) {
        this.doCommute = doCommute;
        this.numStates = state2next.length;
        this.blockSize = (63+numStates) / 64;
        this.words = new long[numStates * blockSize];
        for(int s = 0; s < numStates; ++s) {
            new WrappedBitSet(words, s*blockSize, blockSize, numStates).or(state2next[s]);
        }
    }

    private PowerIntTable(int numStates, long[] words, Function2<PowerIntTable, PowerIntTable, Boolean> doCommute) {
        this.numStates = numStates;
        this.doCommute = doCommute;
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
        return new PowerIntTable(numStates, words, doCommute);
    }

    public PowerIntState next(PowerIntState st) {
        WrappedBitSet s = st.getSubset();
        WrappedBitSet res = new WrappedBitSet(s.length());
        for(int bit = s.nextSetBit(0); bit >= 0; bit = s.nextSetBit(bit+1)) {
            res.or(new WrappedBitSet(words, bit*blockSize, blockSize, numStates));
        }
        return new PowerIntState(st.getBasis(), res);
    }

    private static int total, numCalls;
    public static PowerIntTable composeAll(Sequence<PowerIntTable> fs) {
        long t = System.currentTimeMillis();

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
                int bit = WrappedBitSet.nextSetBit(curWords, ourOffset, blockSize, 0);
                while (bit >= 0) {
                    for (int i = 0; i < blockSize; ++i) {
                        newWords[ourOffset + i] |= nextWords[bit * blockSize + i];
                    }
                    bit = WrappedBitSet.nextSetBit(curWords, ourOffset, blockSize, bit + 1);
                }
            }
            long[] tmp = curWords;
            curWords = newWords;
            newWords = tmp;
        }

        total += System.currentTimeMillis() - t;
        numCalls++;
        if(numCalls % 1000 == 0) {
            System.out.println("Total " + total + " / " + numCalls);
        }
        return new PowerIntTable(numStates, curWords, first.doCommute);
    }
}
