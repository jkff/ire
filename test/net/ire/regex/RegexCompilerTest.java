package net.ire.regex;

import net.ire.fa.DFA;
import net.ire.fa.PowerIntState;
import org.junit.Test;

import java.util.BitSet;

import static net.ire.regex.RegexCompiler.*;
import static org.junit.Assert.*;

/**
 * Created on: 04.09.2010 11:42:12
 */
public class RegexCompilerTest {
    @Test
    public void testEmpty() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(new Empty(), 0)), 1);
        assertTerminatesPatterns(dfa, "", true);
        assertTerminatesPatterns(dfa, "a", false);
    }

    @Test
    public void testAnyChar() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(CharacterClass.ANY_CHAR, 0)), 1);
        assertTerminatesPatterns(dfa, "", false);
        assertTerminatesPatterns(dfa, "a", true);
        assertTerminatesPatterns(dfa, "ab", false);
    }

    @Test
    public void testAB() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(
                new Sequence(CharacterClass.oneOf("a"), CharacterClass.oneOf("b")), 0)), 1);
        assertTerminatesPatterns(dfa, "", false);
        assertTerminatesPatterns(dfa, "a", false);
        assertTerminatesPatterns(dfa, "ab", true);
        assertTerminatesPatterns(dfa, "abc", false);
    }

    @Test
    public void testAorB() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(
                new Alternative(CharacterClass.oneOf("a"), CharacterClass.oneOf("b")), 0)), 1);
        assertTerminatesPatterns(dfa, "", false);
        assertTerminatesPatterns(dfa, "a", true);
        assertTerminatesPatterns(dfa, "b", true);
        assertTerminatesPatterns(dfa, "c", false);
        assertTerminatesPatterns(dfa, "ab", false);
        assertTerminatesPatterns(dfa, "ca", false);
    }

    @Test
    public void testAorMore() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(
                new OnceOrMore(CharacterClass.oneOf("a")), 0)), 1);
        assertTerminatesPatterns(dfa, "", false);
        assertTerminatesPatterns(dfa, "a", true);
        assertTerminatesPatterns(dfa, "aa", true);
        assertTerminatesPatterns(dfa, "aaa", true);
        assertTerminatesPatterns(dfa, "b", false);
        assertTerminatesPatterns(dfa, "ba", false);
        assertTerminatesPatterns(dfa, "ab", false);
    }

    @Test
    public void testABorMore() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(
                new OnceOrMore(new Sequence(CharacterClass.oneOf("a"), CharacterClass.oneOf("b"))), 0)), 1);
        assertTerminatesPatterns(dfa, "", false);
        assertTerminatesPatterns(dfa, "a", false);
        assertTerminatesPatterns(dfa, "ab", true);
        assertTerminatesPatterns(dfa, "aba", false);
        assertTerminatesPatterns(dfa, "abab", true);
        assertTerminatesPatterns(dfa, "ababa", false);
        assertTerminatesPatterns(dfa, "aabab", false);
    }

    @Test
    public void testOptional() {
        DFA<Character,PowerIntState> dfa = toDFA(toNFA(new Labeled(
                new Optional(new Sequence(CharacterClass.oneOf("a"), CharacterClass.oneOf("b"))), 0)), 1);
        assertTerminatesPatterns(dfa, "", true);
        assertTerminatesPatterns(dfa, "a", false);
        assertTerminatesPatterns(dfa, "ab", true);
        assertTerminatesPatterns(dfa, "aba", false);
        assertTerminatesPatterns(dfa, "cab", false);
        assertTerminatesPatterns(dfa, "abab", false);
    }

    private void assertTerminatesPatterns(
            DFA<Character,PowerIntState> dfa, String input, boolean... terminatesWhichPatterns)
    {
        PowerIntState s = dfa.getInitialState();
        for(char c : input.toCharArray()) {
            s = dfa.transfer(c).next(s);
        }
        BitSet bs = s.getTerminatedPatterns();
        for(int i = 0; i < terminatesWhichPatterns.length; ++i) {
            assertEquals("Pattern " + i, terminatesWhichPatterns[i], (bs == null) ? false : bs.get(i));
        }
    }
}
