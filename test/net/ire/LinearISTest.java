package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.IntState;
import net.ire.fa.PowerIntState;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created on: 31.07.2010 12:16:56
 */
public class LinearISTest {
    @Test
    public void testABConDFA() {
        DFABuilder forward = new DFABuilder(4, 0, 1);
        forward.state(0).transitions('a', 1, null, 0);
        forward.state(1).transitions('b', 2, null, 0);
        forward.state(2).transitions('c', 3, null, 0);
        forward.state(3, 0).transitions(null, 3);

        DFABuilder backward = new DFABuilder(4, 0, 1);
        backward.state(0).transitions('c', 1, null, 0);
        backward.state(1).transitions('b', 2, null, 0);
        backward.state(2).transitions('a', 3, null, 0);
        backward.state(3, 0).transitions(null, 3);

        BiDFA<Character,IntState> bidfa = new BiDFA<Character, IntState>(forward.build(), backward.build());
        LinearIS<?> is = new LinearIS<IntState>("xxxcabccccc", bidfa);

        List<Match> matches = new ArrayList<Match>();
        for(Match m : is.getMatches()) {
            matches.add(m);
        }
        assertEquals(1, matches.size());
        assertEquals(3, matches.get(0).length());
        assertEquals(0, matches.get(0).whichPattern());
        assertEquals(4, matches.get(0).startPos());
    }

    @Test
    public void testABConNFA() {
        NFABuilder forward = new NFABuilder(4, 0, 1);
        forward.state(0).transitions('a', 1, null, 0);
        forward.state(1).transitions('b', 2, null, 0);
        forward.state(2).transitions('c', 3, null, 0);
        forward.state(3, 0).transitions(null, 3);

        NFABuilder backward = new NFABuilder(4, 0, 1);
        backward.state(0).transitions('c', 1, null, 0);
        backward.state(1).transitions('b', 2, null, 0);
        backward.state(2).transitions('a', 3, null, 0);
        backward.state(3, 0).transitions(null, 3);

        BiDFA<Character,PowerIntState> bidfa = new BiDFA<Character, PowerIntState>(
                forward.build(), backward.build());
        LinearIS<?> is = new LinearIS<PowerIntState>("xxxcabccccc", bidfa);

        List<Match> matches = new ArrayList<Match>();
        for(Match m : is.getMatches()) {
            matches.add(m);
        }
        assertEquals(1, matches.size());
        assertEquals(3, matches.get(0).length());
        assertEquals(0, matches.get(0).whichPattern());
        assertEquals(4, matches.get(0).startPos());
    }
    
    @Test
    public void testABorAConNFA() {
        NFABuilder forward = new NFABuilder(5, 0, 2);
        forward.state(0).transitions('a', 1, 'a', 3, null, 0);
        forward.state(1).transitions('b', 2, null, 0);
        forward.state(2, 0).transitions(null, 0);
        forward.state(3).transitions('c', 4, null, 0);
        forward.state(4, 1).transitions(null, 0);

        NFABuilder backward = new NFABuilder(5, 0, 2);
        backward.state(0).transitions('b', 1, 'c', 3, null, 0);
        backward.state(1).transitions('a', 2, null, 0);
        backward.state(2, 0).transitions(null, 0);
        backward.state(3).transitions('a', 4, null, 0);
        backward.state(4, 1).transitions(null, 0);

        BiDFA<Character,PowerIntState> bidfa = new BiDFA<Character, PowerIntState>(
                forward.build(), backward.build());
        LinearIS<?> is = new LinearIS<PowerIntState>("xxxcabcacccc", bidfa);

        List<Match> matches = new ArrayList<Match>();
        for(Match m : is.getMatches()) {
            matches.add(m);
        }
        assertEquals(2, matches.size());
        assertEquals(2, matches.get(0).length());
        assertEquals(0, matches.get(0).whichPattern());
        assertEquals(4, matches.get(0).startPos());
        
        assertEquals(2, matches.get(1).length());
        assertEquals(1, matches.get(1).whichPattern());
        assertEquals(7, matches.get(1).startPos());
    }
}
