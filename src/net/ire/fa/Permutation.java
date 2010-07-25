package net.ire.fa;

/**
 * Created on: 22.07.2010 23:49:39
 */
public class Permutation implements TransferFunction<Integer> {
    private int[] perm;

    public Permutation(int[] perm) {
        this.perm = perm;
    }

    public Permutation followedBy(TransferFunction<Integer> other) {
        int[] res = new int[perm.length];
        for(int i = 0; i < res.length; ++i) {
            res[i] = ((Permutation) other).perm[this.perm[i]];
        }
        return new Permutation(res);
    }

    public Integer next(Integer x) {
        return perm[x];
    }
}
