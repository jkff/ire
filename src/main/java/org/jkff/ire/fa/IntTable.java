package org.jkff.ire.fa;

import org.jkff.ire.util.Reducer;

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

    public static Reducer<TransferFunction<IntState>> REDUCER = new Reducer<TransferFunction<IntState>>() {
        public TransferFunction<IntState> compose(
                TransferFunction<IntState> a, TransferFunction<IntState> b)
        {
            if(a == null)
                return b;
            if(b == null)
                return a;
            return ((IntTable)a).followedBy((IntTable)b);
        }

        public TransferFunction<IntState> composeAll(Sequence<TransferFunction<IntState>> ts) {
            TransferFunction<IntState> res = ts.get(0);
            for(int i = 1; i < ts.length(); ++i) {
                res = compose(res, ts.get(i));
            }
            return res;
        }
    };

    private IntTable followedBy(IntTable other) {
        int[] res = new int[table.length];
        for(int i = 0; i < res.length; ++i) {
            res[i] = other.table[this.table[i]];
        }
        return new IntTable(states, res);
    }

    public IntState next(IntState x) {
        return states[table[x.getIndex()]];
    }
}
