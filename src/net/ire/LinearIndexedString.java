package net.ire;

import net.ire.fa.DFA;
import net.ire.fa.MultiDFA;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created on: 23.07.2010 9:23:42
 */
public class LinearIndexedString implements IndexedString<LinearIndexedString> {
    private CharSequence cs;
    private MultiDFA<Character> mdfa;

    public LinearIndexedString(CharSequence cs, MultiDFA<Character> mdfa) {
        this.cs = cs;
        this.mdfa = mdfa;
    }

    public Iterable<Match> getMatches() {
        List<Match> res = new ArrayList<Match>();
        int state = 0;
        DFA<Character> dfa = mdfa.getDFA();
        for(int i = 0; i < length(); ++i) {
            BitSet term = dfa.getTerminatedPatterns(state);
            if(!term.isEmpty()) {
                for(int bit = term.nextSetBit(0); bit != -1; bit = term.nextSetBit(bit)) {
                    DFA<Character> subDFA = mdfa.getSubRDFA(bit);
                    int substate = 0;
                    for(int j = i; j >= 0; --j) {
                        BitSet subterm = subDFA.getTerminatedPatterns(substate);
                        if(!subterm.isEmpty()) {
                            res.add(new Match(bit, j, i-j+1));
                            break;
                        }
                    }
                }
            }
            state = dfa.transfer(charAt(i)).next(state);
        }
        return res;
    }

    public int length() {
        return cs.length();
    }

    public char charAt(int index) {
        return cs.charAt(index);
    }

    public String toString() {
        return cs.toString();
    }

    public LinearIndexedString subSequence(int start, int end) {
        return new LinearIndexedString(cs.subSequence(start, end), mdfa);
    }

    public Pair<LinearIndexedString, LinearIndexedString> splitBefore(int index) {
        return Pair.of(new LinearIndexedString(cs.subSequence(0, index), mdfa), new LinearIndexedString(cs.subSequence(index, cs.length() - index), mdfa));
    }

    public Pair<LinearIndexedString, LinearIndexedString> splitBeforeRise(Predicate<LinearIndexedString> pred) {
        for(int i = 0; i < length(); ++i) {
            if(pred.isTrueFor(subSequence(0, i)))
                return splitBefore(i);
        }
        return splitBefore(length());
    }

    public LinearIndexedString prepend(char c) {
        return new LinearIndexedString(c+cs.toString(), mdfa);
    }

    public LinearIndexedString append(char c) {
        return new LinearIndexedString(cs.toString()+c, mdfa);
    }

    public LinearIndexedString append(LinearIndexedString other) {
        return new LinearIndexedString(cs.toString() + other.toString(), mdfa);
    }
}
