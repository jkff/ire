package net.ire.fa;

/**
 * Created on: 25.07.2010 13:34:11
 */
public class MultiDFA<C> {
    private DFA<C> dfa;
    private DFA<C>[] subRDFAs;

    public MultiDFA(DFA<C> dfa, DFA<C>[] subRDFAs) {
        this.dfa = dfa;
        this.subRDFAs = subRDFAs;
    }

    public DFA<C> getDFA() {
        return dfa;
    }

    public DFA<C> getSubRDFA(int index) {
        return subRDFAs[index];
    }
}
