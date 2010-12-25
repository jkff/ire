package org.jkff.ire;

import org.jkff.ire.fa.BiDFA;
import org.jkff.ire.fa.PowerIntState;
import org.jkff.ire.rope.RopeBasedIS;

/**
 * Created on: 01.09.2010 23:44:51
 */
public class DFARopePatternSet implements PatternSet {
    private BiDFA<Character, PowerIntState> bidfa;

    public DFARopePatternSet(BiDFA<Character, PowerIntState> bidfa) {
        this.bidfa = bidfa;
    }

    public IndexedString match(String s) {
        return new RopeBasedIS<PowerIntState>(bidfa, s);
    }

    public IndexedString match(String s, int blockSize) {
        return new RopeBasedIS<PowerIntState>(bidfa, s, blockSize);
    }
}
