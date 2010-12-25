package org.jkff.ire;

import org.jkff.ire.fa.BiDFA;
import org.jkff.ire.fa.State;
import org.jkff.ire.util.Function2;
import org.jkff.ire.util.Pair;
import org.jkff.ire.util.Predicate;

import org.jkff.ire.util.WrappedBitSet;
import java.util.List;

import static org.jkff.ire.util.CollectionFactory.newArrayList;

/**
 * Created on: 31.07.2010 12:19:28
 */
public class DFAMatcher {
    @SuppressWarnings("unchecked")
    public static <ST extends State>
        Iterable<Match> getMatches(
            final BiDFA<Character, ST> bidfa, final DFAIndexedString<ST> string)
    {
        final ST initial = bidfa.getForward().getInitialState();

        Function2<SP<ST>, IndexedString, SP<ST>> addString = new Function2<SP<ST>, IndexedString, SP<ST>>() {
            public SP<ST> applyTo(SP<ST> sp, IndexedString s) {
                return new SP<ST>(((DFAIndexedString<ST>) s).getForward().next(sp.state), sp.pos+s.length());
            }
        };

        Function2<SP<ST>, Character, SP<ST>> addChar = new Function2<SP<ST>, Character, SP<ST>>() {
            public SP<ST> applyTo(SP<ST> sp, Character c) {
                return new SP<ST>(bidfa.getForward().transfer(c).next(sp.state), sp.pos+1);
            }
        };

        List<Match> res = newArrayList();

        int shift = 0;

        SP<ST> matchStartState = new SP<ST>(initial, 0);
        IndexedString rem = string;
        IndexedString seen = string.subSequence(0,0);

        while(true) {
            Pair<IndexedString, IndexedString> p = rem.splitAfterRise(
                    matchStartState, addString, addChar, DFAMatcher.<ST>hasForwardMatchAfter(shift));
            if(p == null)
                break;

            DFAIndexedString<ST> matchingPrefix = (DFAIndexedString<ST>) p.first;
            rem = p.second;
            seen = seen.append(matchingPrefix);

            final ST stateAfterMatch = matchingPrefix.getForward().next(matchStartState.state);
            WrappedBitSet term = stateAfterMatch.getTerminatedPatterns();

            ST backwardInitial = bidfa.getBackward().getInitialState();

            ST nextMatchStart = stateAfterMatch;

            for(int bit = term.nextSetBit(0); bit >= 0; bit = term.nextSetBit(bit+1)) {
                final int bit2 = bit;

                Function2<ST, IndexedString, ST> addStringBack = new Function2<ST, IndexedString, ST>() {
                    public ST applyTo(ST st, IndexedString s) {
                        return ((DFAIndexedString<ST>) s).getBackward().next(st);
                    }
                };

                Function2<ST, Character, ST> addCharBack = new Function2<ST, Character, ST>() {
                    public ST applyTo(ST st, Character c) {
                        return bidfa.getBackward().transfer(c).next(st);
                    }
                };

                Predicate<ST> startsThisMatch = new Predicate<ST>() {
                    public boolean isTrueFor(ST state) {
                        WrappedBitSet tp = state.getTerminatedPatterns();
                        return tp!=null && tp.get(bit2);
                    }
                };

                int len = seen.splitAfterBackRise(
                        backwardInitial, addStringBack, addCharBack, startsThisMatch).second.length();
                int startPos = seen.length() - len;
                res.add(new Match(bit, startPos, len));

                nextMatchStart = bidfa.getForward().resetTerminatedPattern(nextMatchStart, bit);
            }

            matchStartState = new SP<ST>(nextMatchStart, matchingPrefix.length() + 1);
        }

        return res;
    }

    private static <ST extends State> Predicate<SP<ST>> hasForwardMatchAfter(final int pos) {
        return new Predicate<SP<ST>>() {
            public boolean isTrueFor(SP<ST> sp) {
                return !sp.state.getTerminatedPatterns().isEmpty() && sp.pos >= pos;
            }
        };
    }

    // State and position.
    private static class SP<ST extends State> {
        ST state;
        int pos;

        SP(ST state, int pos) {
            this.state = state;
            this.pos = pos;
        }
    }
}
