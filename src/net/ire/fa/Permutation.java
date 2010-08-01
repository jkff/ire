package net.ire.fa;

/**
 * Created on: 22.07.2010 23:49:39
 */
public class Permutation implements TransferFunction<IntState> {
    private IntState[] states;
    private int[] perm;

    public Permutation(IntState[] states, int[] perm) {
        this.states = states;
        this.perm = perm;
    }

    public Permutation followedBy(TransferFunction<IntState> other) {
        int[] res = new int[perm.length];
        for(int i = 0; i < res.length; ++i) {
            res[i] = ((Permutation) other).perm[this.perm[i]];
        }
        return new Permutation(states, res);
    }

    public IntState next(IntState x) {
        return states[perm[x.getIndex()]];
    }
}
