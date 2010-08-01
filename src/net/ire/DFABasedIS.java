package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.State;
import net.ire.fa.TransferFunction;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created on: 31.07.2010 12:19:28
 */
public abstract class DFABasedIS<S extends DFABasedIS<S, ST>, ST extends State> implements IndexedString<S> {
    protected BiDFA<Character, ST> bidfa;
    protected TransferFunction<ST> forward;
    protected TransferFunction<ST> backward;

    public DFABasedIS(
            TransferFunction<ST> forward, TransferFunction<ST> backward, BiDFA<Character, ST> bidfa) {
        this.forward = forward;
        this.backward = backward;
        this.bidfa = bidfa;
    }

    public Iterable<Match> getMatches() {
        final ST initial = bidfa.getForward().getInitialState();

        // Split on predicate "term != 0".
        // First match ends at right bound of split's left part.
        // Recursively continue on split's right part.
        Predicate<S> hasForwardMatch = new Predicate<S>() {
            public boolean isTrueFor(S s) {
                State state = s.forward.next(initial);
                return !state.getTerminatedPatterns().isEmpty();
            }
        };

        List<Match> res = new ArrayList<Match>();

        DFABasedIS<S,ST> rem = this;
        while(true) {
            Pair<S, S> p = rem.splitAfterRise(hasForwardMatch);
            if(p == null)
                break;

            S matchingPrefix = p.first;
            final State stateLeftEnd = matchingPrefix.forward.next(initial);
            BitSet term = stateLeftEnd.getTerminatedPatterns();

            for(int bit = term.nextSetBit(0); bit >= 0; bit = term.nextSetBit(bit+1)) {
                final int bit2 = bit;

                Predicate<S> startsThisMatch = new Predicate<S>() {
                    public boolean isTrueFor(S s) {
                        ST backwardInitial = bidfa.getBackward().getInitialState();
                        ST state = s.backward.next(backwardInitial);
                        return state.getTerminatedPatterns().get(bit2);
                    }
                };

                int len = matchingPrefix.reverse().splitAfterRise(startsThisMatch).first.length();
                int startPos = matchingPrefix.length() - len;
                res.add(new Match(bit, startPos, len));
            }
            rem = p.second;
        }

        return res;
    }
}
