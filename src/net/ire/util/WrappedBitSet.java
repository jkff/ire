package net.ire.util;

public class WrappedBitSet implements Cloneable {
    private final static int ADDRESS_BITS_PER_WORD = 6;
    private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;

    private static final long WORD_MASK = 0xffffffffffffffffL;

    private long[] words;
    private int offset;
    private int length;

    private int numBits;

    public WrappedBitSet(int nbits) {
        words = new long[wordIndex(nbits -1) + 1];
        this.offset = 0;
        this.length = words.length;
        this.numBits = nbits;
    }

    public WrappedBitSet(long[] words, int offset, int length, int numBits) {
        this.words = words;
        this.offset = offset;
        this.length = length;
        this.numBits = numBits;
    }

    private int wordIndex(int bitIndex) {
        return offset + (bitIndex >> ADDRESS_BITS_PER_WORD);
    }

    public void set(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);
    	words[wordIndex] |= (1L << bitIndex);
    }

    public void clear(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);
        words[wordIndex] &= ~(1L << bitIndex);
    }

    public boolean get(int bitIndex) {
	    int wordIndex = wordIndex(bitIndex);
	    return ((words[wordIndex] & (1L << bitIndex)) != 0);
    }

    public int nextSetBit(int fromIndex) {
        int u = wordIndex(fromIndex)-offset;
    	long word = words[offset+u] & (WORD_MASK << fromIndex);
        while (true) {
            if (word != 0)
                return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
            if (++u == length)
                return -1;
            word = words[offset+u];
        }
    }

    public int length() {
        return length;
    }

    public boolean isEmpty() {
        for(int i = offset; i < offset + length; ++i) {
            if(words[i] != 0)
                return false;
        }
        return true;
    }

    public int cardinality() {
        int sum = 0;
        for(int i = offset; i < offset + length; ++i)  {
            sum += Long.bitCount(words[i]);
        }
        return sum;
    }

    public void and(WrappedBitSet set) {
        for (int i = 0; i < length; ++i)
            words[offset+i] &= set.words[set.offset+i];
    }

    public void or(WrappedBitSet set) {
        for (int i = 0; i < length; ++i)
            words[offset+i] |= set.words[set.offset+i];
    }

    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append('{');
        int i = nextSetBit(0);
        if (i != -1) {
            b.append(i);
            for (i = nextSetBit(i+1); i >= 0; i = nextSetBit(i+1)) {
                b.append(", ").append(i);
            }
        }

        b.append('}');
        return b.toString();
    }

    public WrappedBitSet makeCopy() {
        long[] words = new long[length];
        System.arraycopy(this.words, offset, words, 0, length);
        return new WrappedBitSet(words, 0, words.length, numBits);
    }
}
