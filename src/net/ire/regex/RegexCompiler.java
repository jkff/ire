package net.ire.regex;

import net.ire.DFARopePatternSet;
import net.ire.PatternSet;
import net.ire.fa.*;
import net.ire.util.Pair;

import java.util.*;

import static net.ire.util.CollectionFactory.*;

/**
 * Created on: 01.09.2010 23:43:07
 */
public class RegexCompiler {
    public static PatternSet compile(List<RxNode> roots) {
        return new DFARopePatternSet(compileToBiDFA(roots));
    }

    static BiDFA<Character, PowerIntState> compileToBiDFA(List<RxNode> roots) {
        List<RxNode> reversedRoots = newArrayList();
        for(RxNode root : roots) {
            reversedRoots.add(reverse(root));
        }
        return new BiDFA<Character, PowerIntState>(compileToDFA(roots), compileToDFA(reversedRoots));
    }

    static DFA<Character, PowerIntState> compileToDFA(List<RxNode> rxNodes) {
        if(rxNodes.isEmpty()) {
            throw new IllegalArgumentException("Pattern list can't be empty");
        }
        List<RxNode> labeled = newArrayList();
        for(int i = 0; i < rxNodes.size(); ++i) {
            labeled.add(new Labeled(rxNodes.get(i), i));
        }
        RxNode alt = labeled.get(0);
        for(int i = 1; i < rxNodes.size(); ++i) {
            alt = new Alternative(alt, labeled.get(i));
        }

        return toDFA(toNFA(alt), rxNodes.size());
    }

    static DFA<Character, PowerIntState> toDFA(NFA nfa, int numPatterns) {
        final Map<NFA.Node, Set<NFA.Node>> node2closure = newHashMap();

        Set<NFA.Node> allNodes = dfs(nfa.begin, true);
        for(NFA.Node node : allNodes) {
            node2closure.put(node, dfs(node, false));
        }

        final int numStates = allNodes.size();

        final Map<NFA.Node, Integer> node2id = newHashMap();
        final NFA.Node[] id2node = allNodes.toArray(new NFA.Node[allNodes.size()]);

        for(int i = 0; i < id2node.length; ++i) {
            node2id.put(id2node[i], i);
        }

        State[] basis = new State[numStates];
        for(int i = 0; i < numStates; ++i) {
            BitSet terminatedPatterns = new BitSet(numPatterns);
            for(NFA.Node node : node2closure.get(id2node[i])) {
                if(node.patternId != -1)
                    terminatedPatterns.set(node.patternId);
            }
            basis[i] = new IntState(i, terminatedPatterns);
        }

        TransferTable<Character,PowerIntState> transfer = new TransferTable<Character, PowerIntState>() {
            private Map<Character, TransferFunction<PowerIntState>> transfer = newHashMap();

            public TransferFunction<PowerIntState> forToken(Character token) {
                TransferFunction<PowerIntState> f = transfer.get(token);
                if(f == null) {
                    transfer.put(token, f = computeTransferFor(token));
                }
                return f;
            }

            private TransferFunction<PowerIntState> computeTransferFor(Character token) {
                BitSet[] state2next = new BitSet[numStates];
                for(int i = 0; i < numStates; ++i) {
                    BitSet res = new BitSet(numStates);
                    NFA.Node node = id2node[i];
                    for(NFA.Node eReachableSrc : node2closure.get(node)) {
                        for (Pair<CharacterClass, NFA.Node> out : eReachableSrc.out) {
                            if(out.first != null && out.first.acceptsChar(token)) {
                                for(NFA.Node eReachableDest : node2closure.get(out.second)) {
                                    res.set(node2id.get(eReachableDest));
                                }
                            }
                        }
                    }
                    state2next[i] = res;
                }
                return new PowerIntTable(state2next);
            }
        };

        BitSet justInitial = new BitSet(numStates);
        for(NFA.Node node : node2closure.get(nfa.begin)) {
            justInitial.set(node2id.get(node));
        }
        PowerIntState initial = new PowerIntState(basis, justInitial);

        return new DFA<Character, PowerIntState>(transfer, initial);
    }

    static Set<NFA.Node> dfs(NFA.Node origin, boolean acceptNonEps) {
        Set<NFA.Node> res = newHashSet();
        Stack<NFA.Node> toVisit = new Stack<NFA.Node>();
        toVisit.add(origin);
        while(!toVisit.isEmpty()) {
            NFA.Node node = toVisit.pop();
            if(!res.add(node))
                continue;
            for (Pair<CharacterClass, NFA.Node> out : node.out) {
                if(out.first == null || acceptNonEps) {
                    toVisit.push(out.second);
                }
            }
        }
        return res;
    }

    static NFA toNFA(RxNode rxNode) {
        if(rxNode instanceof Alternative) {
            Alternative x = (Alternative) rxNode;
            NFA res = new NFA(new NFA.Node(), new NFA.Node());
            NFA a = toNFA(x.a), b = toNFA(x.b);
            res.begin.transition(null, a.begin);
            res.begin.transition(null, b.begin);
            a.end.transition(null, res.end);
            b.end.transition(null, res.end);
            return res;
        } else if(rxNode instanceof CharacterClass) {
            NFA res = new NFA(new NFA.Node(), new NFA.Node());
            res.begin.transition((CharacterClass) rxNode, res.end);
            return res;
        } else if(rxNode instanceof Empty) {
            NFA res = new NFA(new NFA.Node(), new NFA.Node());
            res.begin.transition(null, res.end);
            return res;
        } else if(rxNode instanceof OnceOrMore) {
            OnceOrMore x = (OnceOrMore) rxNode;
            NFA res = toNFA(x.a);
            res.end.transition(null, res.begin);
            return res;
        } else if(rxNode instanceof Optional) {
            Optional x = (Optional) rxNode;
            NFA res = toNFA(x.a);
            res.begin.transition(null, res.end);
            return res;
        } else if(rxNode instanceof Sequence) {
            Sequence x = (Sequence) rxNode;
            NFA a = toNFA(x.a), b = toNFA(x.b);
            NFA res = new NFA(a.begin, b.end);
            a.end.transition(null, b.begin);
            return res;
        } else if(rxNode instanceof Labeled) {
            Labeled x = (Labeled) rxNode;
            NFA a = toNFA(x.a);
            a.end.patternId = x.patternId;
            return a;
        } else {
            throw new UnsupportedOperationException("Unsupported node type " + rxNode.getClass());
        }
    }

    static class NFA {
        final Node begin, end;

        public NFA(Node begin, Node end) {
            this.begin = begin;
            this.end = end;
        }

        static class Node {
            List<Pair<CharacterClass, Node>> out = newArrayList();
            int patternId = -1;

            void transition(CharacterClass cc, Node dest) {
                out.add(Pair.<CharacterClass, Node>of(cc, dest));
            }
        }
    }

    private static RxNode reverse(RxNode rxNode) {
        if(rxNode instanceof Alternative) {
            Alternative x = (Alternative) rxNode;
            return new Alternative(reverse(x.a), reverse(x.b));
        } else if(rxNode instanceof CharacterClass) {
            return rxNode;
        } else if(rxNode instanceof Empty) {
            return rxNode;
        } else if(rxNode instanceof OnceOrMore) {
            OnceOrMore x = (OnceOrMore) rxNode;
            return new OnceOrMore(reverse(x.a));
        } else if(rxNode instanceof Optional) {
            Optional x = (Optional) rxNode;
            return new Optional(reverse(x.a));
        } else if(rxNode instanceof Sequence) {
            Sequence x = (Sequence) rxNode;
            // The only interesting case.
            return new Sequence(reverse(x.b), reverse(x.a));
        } else if(rxNode instanceof Labeled) {
            Labeled x = (Labeled) rxNode;
            return new Labeled(reverse(x.a), x.patternId);
        } else {
            throw new UnsupportedOperationException("Unsupported node type " + rxNode.getClass());
        }
    }
}
