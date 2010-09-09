package net.ire;

import net.ire.fa.*;

import net.ire.util.WrappedBitSet;

import java.util.List;

import static net.ire.util.CollectionFactory.newArrayList;

/**
 * Created on: 01.08.2010 13:47:21
 */
public class NFABuilder {
    private IntState[] basisStates;
    private int numPatterns;
    private int initialState;
    private List<Transition> transitions = newArrayList();

    public NFABuilder(int numBasisStates, int initialState, int numPatterns) {
        this.basisStates = new IntState[numBasisStates];
        this.initialState = initialState;
        this.numPatterns = numPatterns;
    }

    public StateBuilder state(int i, int... termPatterns) {
        return new StateBuilder(i, termPatterns);
    }

    public DFA<Character, PowerIntState> build() {
        final WrappedBitSet[][] char2state2next = new WrappedBitSet[256][basisStates.length];
        for(int i = 0; i < 256; ++i)
            for(int j = 0; j < basisStates.length; ++j)
                char2state2next[i][j] = new WrappedBitSet(basisStates.length);
        
        for(Transition t : transitions) {
            if(t.c != null) {
                char2state2next[t.c][t.from].set(t.to);
            }
        }
        for(Transition t : transitions) {
            if(t.c == null) {
                for(int i = 0; i < 256; ++i)
                    if(char2state2next[i][t.from].isEmpty())
                        char2state2next[i][t.from].set(t.to);
            }
        }
        TransferTable<Character, PowerIntState> transfer = new TransferTable<Character, PowerIntState>() {
            public TransferFunction<PowerIntState> forToken(Character token) {
                return new PowerIntTable(char2state2next[token], doCommute);
            }
        };

        WrappedBitSet justInitial = new WrappedBitSet(basisStates.length);
        justInitial.set(initialState);
        return new DFA<Character, PowerIntState>(transfer, new PowerIntState(basisStates, justInitial));
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
            basisStates[state] = new IntState(state, t);
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