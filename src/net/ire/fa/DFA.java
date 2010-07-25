package net.ire.fa;

import java.util.BitSet;
import java.util.Map;

/**
 * Created on: 22.07.2010 23:54:27
 */
public class DFA<C> {
    private State[] states;
    private Map<C,TransferFunction<Integer>> transfer;

    public DFA(State[] states, Map<C,TransferFunction<Integer>> transfer) {
        this.states = states;
        this.transfer = transfer;
    }

    public TransferFunction<Integer> transfer(C token) {
        return transfer.get(token);
    }

    public BitSet getTerminatedPatterns(int state) {
        return states[state].terminatedPatterns;
    }

    private class State {
        // For which of the patterns is this state terminatedPatterns?
        private BitSet terminatedPatterns;

        private State(BitSet terminatedPatterns) {
            this.terminatedPatterns = terminatedPatterns;
        }
    }
}
