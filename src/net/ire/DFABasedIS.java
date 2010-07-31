package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.TransferFunction;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created on: 31.07.2010 12:19:28
 */
public abstract class DFABasedIS<S extends DFABasedIS<S>> implements IndexedString<S> {
    protected BiDFA<Character> bidfa;
    protected TransferFunction<Integer> forward;
    protected TransferFunction<Integer> backward;

    public DFABasedIS(TransferFunction<Integer> forward, TransferFunction<Integer> backward, BiDFA<Character> bidfa) {
        this.forward = forward;
        this.backward = backward;
        this.bidfa = bidfa;
    }

    public Iterable<Match> getMatches() {
        // Split on predicate "term != 0".
        // First match ends at right bound of split's left part.
        // Recursively continue on split's right part.
        Predicate<S> hasForwardMatch = new Predicate<S>() {
            public boolean isTrueFor(S s) {
                int state = s.forward.next(0);
                return !bidfa.getForward().getTerminatedPatterns(state).isEmpty();
            }
        };

        List<Match> res = new ArrayList<Match>();

        DFABasedIS<S> rem = this;
        while(true) {
            Pair<S, S> p = rem.splitAfterRise(hasForwardMatch);
            if(p == null)
                break;

            S matchingPrefix = p.first;
            final int stateLeftEnd = matchingPrefix.forward.next(0);
            BitSet term = bidfa.getForward().getTerminatedPatterns(stateLeftEnd);

            for(int bit = term.nextSetBit(0); bit >= 0; bit = term.nextSetBit(bit+1)) {
                final int bit2 = bit;

                Predicate<S> startsThisMatch = new Predicate<S>() {
                    public boolean isTrueFor(S s) {
                        int state = s.backward.next(0);
                        return bidfa.getBackward().getTerminatedPatterns(state).get(bit2);
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
