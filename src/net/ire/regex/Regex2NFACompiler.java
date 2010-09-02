package net.ire.regex;

import net.ire.DFARopePatternSet;
import net.ire.PatternSet;
import net.ire.fa.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created on: 01.09.2010 23:43:07
 */
public class Regex2NFACompiler {
    public static PatternSet compile(List<Node> roots) {
        return new DFARopePatternSet(compileToBiDFA(roots));
    }

    private static BiDFA<Character, PowerIntState> compileToBiDFA(List<Node> roots) {
        List<DFA<Character, PowerIntState>> forwards = new ArrayList<DFA<Character, PowerIntState>>();
        List<DFA<Character, PowerIntState>> backwards = new ArrayList<DFA<Character, PowerIntState>>();
        for(Node root : roots) {
            forwards.add(compileToDFA(root));
            backwards.add(compileToDFA(reverse(root)));
        }
        return new BiDFA<Character, PowerIntState>(alternateSinglePatternDFAs(forwards), alternateSinglePatternDFAs(backwards));
    }

    private static DFA<Character, PowerIntState> compileToDFA(Node node) {
        throw new UnsupportedOperationException();
    }

    private static Node reverse(Node node) {
        if(node instanceof Alternative) {
            Alternative x = (Alternative) node;
            return new Alternative(reverse(x.a), reverse(x.b));
        } else if(node instanceof CharacterClass) {
            return node;
        } else if(node instanceof Empty) {
            return node;
        } else if(node instanceof OnceOrMore) {
            OnceOrMore x = (OnceOrMore) node;
            return new OnceOrMore(reverse(x.a));
        } else if(node instanceof Optional) {
            Optional x = (Optional) node;
            return new Optional(reverse(x.a));
        } else if(node instanceof Sequence) {
            Sequence x = (Sequence) node;
            // The only interesting case.
            return new Sequence(reverse(x.b), reverse(x.a));
        } else {
            throw new UnsupportedOperationException("Unsupported node type " + node.getClass());
        }
    }

    private static DFA<Character,PowerIntState> alternateSinglePatternDFAs(final List<DFA<Character,PowerIntState>> dfas) {
        int numPatterns = dfas.size();

        List<State> basisList = new ArrayList<State>();
        basisList.add(new IntState(0, new BitSet(numPatterns)));
        for(DFA<Character,PowerIntState> dfa : dfas) {
            for(State s : dfa.getInitialState().getBasis())
                basisList.add(s);
        }
        final State[] basis = basisList.toArray(new State[basisList.size()]);

        TransferTable<Character, PowerIntState> transfer = new TransferTable<Character, PowerIntState>() {
            private PowerIntTable[] token2table = new PowerIntTable[1+Character.MAX_VALUE];

            public TransferFunction<PowerIntState> forToken(Character token) {
                PowerIntTable table = token2table[token];
                if(table == null)
                    table = token2table[token] = computeTableFor(token);
                return table;
            }

            private PowerIntTable computeTableFor(Character token) {
                BitSet[] state2next = new BitSet[basis.length];
                // 0th state goes to OR of destinations of all initial states,
                // because its epsilon-connected to them.
                // All other states' tables are simply concatenated
                // (with a trivial translation of indices).
                BitSet fromInitial = new BitSet(basis.length);
                for(DFA<Character,PowerIntState> dfa : dfas)
                    fromInitial.or(dfa.transfer(token).next(dfa.getInitialState()).getSubset());

                state2next[0] = fromInitial;

                int offset = 1;
                for (DFA<Character, PowerIntState> dfa : dfas) {
                    TransferFunction<PowerIntState> tf = dfa.transfer(token);
                    State[] curBasis = dfa.getInitialState().getBasis();
                    BitSet singleton = new BitSet(curBasis.length);
                    for (int b = 0; b < curBasis.length; ++b) {
                        singleton.set(b);
                        BitSet curDest = tf.next(new PowerIntState(curBasis, singleton)).getSubset();
                        BitSet translatedDest = new BitSet(basis.length);
                        for(int bit = curDest.nextSetBit(0); bit != -1; bit = curDest.nextSetBit(bit+1)) {
                            translatedDest.set(1 + offset + bit);
                        }
                        state2next[offset+b] = translatedDest;
                        singleton.clear(b);
                    }
                    offset += curBasis.length;
                }

                return new PowerIntTable(state2next);
            }
        };

        BitSet justInitial = new BitSet(basis.length);
        justInitial.set(0);
        PowerIntState initial = new PowerIntState(basis, justInitial);

        return new DFA<Character, PowerIntState>(transfer, initial);
    }
}
