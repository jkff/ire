package net.ire.regex;

import net.ire.DFARopePatternSet;
import net.ire.PatternSet;
import net.ire.fa.*;
import net.ire.util.Pair;

import java.util.*;

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
        return toDFA(eClosure(toNFA(node)));
    }

    private static DFA<Character, PowerIntState> toDFA(NFA nfa) {

    }

    private static NFA eClosure(NFA nfa) {
        Map<Set<NFA.Node>, NFA.Node> closure2newNode = new HashMap<Set<NFA.Node>, NFA.Node>();
        Map<NFA.Node, Set<NFA.Node>> node2closure = new HashMap<NFA.Node, Set<NFA.Node>>();
        Map<NFA.Node, NFA.Node> node2newNode = new HashMap<NFA.Node, NFA.Node>();

        Set<NFA.Node> allNodes = new HashSet<NFA.Node>();
        {
            Set<NFA.Node> toVisit = new HashSet<NFA.Node>();
            toVisit.add(nfa.begin);
            while(!toVisit.isEmpty()) {
                NFA.Node node = toVisit.iterator().next();
                toVisit.remove(node);
                if(allNodes.add(node)) {
                    for (Pair<CharacterClass, NFA.Node> out : node.out) {
                        toVisit.add(out.second);
                    }
                }
            }
        }

        for(NFA.Node node : allNodes) {
            Set<NFA.Node> closure = new HashSet<NFA.Node>();
            closure.add(node);
            while(!closure.isEmpty()) {
                NFA.Node next = closure.iterator().next();
                closure.remove(node);
                if(allNodes.add(node)) {
                    for (Pair<CharacterClass, NFA.Node> out : node.out) {
                        if(out.first == null) { // Sic! Only epsilon transitions
                            closure.add(out.second);
                        }
                    }
                }
            }
            node2closure.put(node, closure);

            NFA.Node newNode = closure2newNode.get(closure);
            if(newNode == null) {
                closure2newNode.put(closure, newNode = new NFA.Node());
            }


        }
    }

    private static NFA toNFA(Node node) {
        if(node instanceof Alternative) {
            Alternative x = (Alternative) node;
            NFA res = new NFA();
            NFA a = toNFA(x.a), b = toNFA(x.b);
            res.begin.transition(null, a.begin);
            res.begin.transition(null, b.begin);
            a.begin.transition(null, res.end);
            b.begin.transition(null, res.end);
            return res;
        } else if(node instanceof CharacterClass) {
            NFA res = new NFA();
            res.begin.transition((CharacterClass)node, res.end);
        } else if(node instanceof Empty) {
            NFA res = new NFA();
            res.begin.transition(null, res.end);
        } else if(node instanceof OnceOrMore) {
            OnceOrMore x = (OnceOrMore) node;
            NFA res = toNFA(x.a);
            res.end.transition(null, res.begin);
            return res;
        } else if(node instanceof Optional) {
            Optional x = (Optional) node;
            NFA res = toNFA(x.a);
            res.begin.transition(null, res.end);
            return res;
        } else if(node instanceof Sequence) {
            Sequence x = (Sequence) node;
            NFA a = toNFA(x.a), b = toNFA(x.b);
            NFA res = new NFA();
            res.begin = a.begin;
            a.end.transition(null, b.begin);
            res.end = a.end;
            return res;
        } else {
            throw new UnsupportedOperationException("Unsupported node type " + node.getClass());
        }
    }

    private static class NFA {
        Node begin = new Node();
        Node end = new Node();

        private static class Node {
            private List<Pair<CharacterClass, Node>> out = new ArrayList<Pair<CharacterClass, Node>>();

            void transition(CharacterClass cc, Node dest) {
                out.add(Pair.<CharacterClass, Node>of(cc, dest));
            }
        }
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
            basisList.addAll(Arrays.asList(dfa.getInitialState().getBasis()));
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
