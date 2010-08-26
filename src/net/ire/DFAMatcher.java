package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.State;
import net.ire.util.Function2;
import net.ire.util.Pair;
import net.ire.util.Predicate;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created on: 31.07.2010 12:19:28
 */
public class DFAMatcher {
    public static <S extends DFAIndexedString<S,ST>, ST extends State>
        Iterable<Match> getMatches(
            final BiDFA<Character, ST> bidfa, final S string)
    {
        final ST initial = bidfa.getForward().getInitialState();

        Function2<ST, S, ST> addString = new Function2<ST, S, ST>() {
            public ST applyTo(ST st, S s) {
                return s.getForward().next(st);
            }
        };
        Function2<ST, Character, ST> addChar = new Function2<ST, Character, ST>() {
            public ST applyTo(ST st, Character c) {
                return bidfa.getForward().transfer(c).next(st);
            }
        };

        // Split on predicate "term != 0".
        // First match ends at right bound of split's left part.
        // Recursively continue on split's right part.
        Predicate<ST> hasForwardMatch = new Predicate<ST>() {
            public boolean isTrueFor(ST state) {
                return !state.getTerminatedPatterns().isEmpty();
            }
        };

        List<Match> res = new ArrayList<Match>();

        DFAIndexedString<S,ST> rem = string;

        int shift = 0;

        while(true) {
            Pair<S, S> p = rem.splitAfterRise(initial, addString, addChar, hasForwardMatch);
            if(p == null)
                break;

            S matchingPrefix = p.first;
            final State stateLeftEnd = matchingPrefix.getForward().next(initial);
            BitSet term = stateLeftEnd.getTerminatedPatterns();

            ST backwardInitial = bidfa.getBackward().getInitialState();

            for(int bit = term.nextSetBit(0); bit >= 0; bit = term.nextSetBit(bit+1)) {
                final int bit2 = bit;

                Function2<ST, S, ST> addStringBack = new Function2<ST, S, ST>() {
                    public ST applyTo(ST st, S s) {
                        return s.getBackward().next(st);
                    }
                };

                Function2<ST, Character, ST> addCharBack = new Function2<ST, Character, ST>() {
                    public ST applyTo(ST st, Character c) {
                        return bidfa.getBackward().transfer(c).next(st);
                    }
                };

                Predicate<ST> startsThisMatch = new Predicate<ST>() {
                    public boolean isTrueFor(ST state) {
                        return state.getTerminatedPatterns().get(bit2);
                    }
                };

                int len = matchingPrefix.splitAfterBackRise(
                        backwardInitial, addStringBack, addCharBack, startsThisMatch).second.length();
                int startPos = matchingPrefix.length() - len + shift;
                res.add(new Match(bit, startPos, len));
            }

            shift += p.first.length();
            
            rem = p.second;
        }

        return res;
    }
}
