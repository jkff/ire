package net.ire;

import net.ire.fa.*;

import net.ire.util.WrappedBitSet;

import java.util.List;
import java.util.Map;

import static net.ire.util.CollectionFactory.newArrayList;
import static net.ire.util.CollectionFactory.newLinkedHashMap;

/**
 * Created on: 01.08.2010 13:47:21
 */
public class DFABuilder {
    private IntState[] states;
    private int numPatterns;
    private int initialState;
    private List<Transition> transitions = newArrayList();

    public DFABuilder(int numStates, int initialState, int numPatterns) {
        this.states = new IntState[numStates];
        this.initialState = initialState;
        this.numPatterns = numPatterns;
    }

    public StateBuilder state(int i, int... termPatterns) {
        return new StateBuilder(i, termPatterns);
    }

    public DFA<Character, IntState> build() {
        final Map<Character,int[]> char2table = newLinkedHashMap();
        for(Transition t : transitions) {
            int[] table = char2table.get(t.c);
            if(table == null)
                char2table.put(t.c, table = new int[states.length]);
            table[t.from] = t.to;
        }
        final int[][] char2state2next = new int[256][states.length];
        for(Transition t : transitions) {
            if(t.c == null) {
                for(int i = 0; i < 256; ++i)
                    char2state2next[i][t.from] = t.to;
            }
        }
        for(Transition t : transitions) {
            if(t.c != null) {
                char2state2next[t.c][t.from] = t.to;
            }
        }
        TransferTable<Character, IntState> transfer = new TransferTable<Character, IntState>() {
            public TransferFunction<IntState> forToken(Character token) {
                return new IntTable(states, char2state2next[token]);
            }
        };
        return new DFA<Character, IntState>(transfer, states[initialState], IntTable.REDUCER) {
            @Override
            public IntState resetTerminatedPattern(IntState state, int pattern) {
                return states[initialState];
            }
        };
    }

    public class StateBuilder {
        private int state;
        private int[] termPatterns;

        public StateBuilder(int state, int... termPatterns) {
            this.state = state;
            this.termPatterns = termPatterns; 
        }

        public void transitions(Object... char2state) {
            for(int i = 0; i < char2state.length; i += 2) {
                transitions.add(new Transition(this.state, (Character)char2state[i], (Integer)char2state[i+1]));
            }
            WrappedBitSet t = new WrappedBitSet(numPatterns);
            for(int tp : termPatterns)
                t.set(tp);
            states[state] = new IntState(state, t);
        }
    }

    private static class Transition {
        int from;
        Character c;
        int to;

        private Transition(int from, Character c, int to) {
            this.from = from;
            this.c = c;
            this.to = to;
        }
    }
}
