package net.ire.fa;

/**
 * Created on: 22.07.2010 23:49:39
 */
public class IntTable implements TransferFunction<IntState> {
    private IntState[] states;
    private int[] table;

    public IntTable(IntState[] states, int[] table) {
        this.states = states;
        this.table = table;
    }

    public IntTable followedBy(TransferFunction<IntState> other) {
        int[] res = new int[table.length];
        for(int i = 0; i < res.length; ++i) {
            res[i] = ((IntTable) other).table[this.table[i]];
        }
        return new IntTable(states, res);
    }

    public IntState next(IntState x) {
        return states[table[x.getIndex()]];
    }
}
