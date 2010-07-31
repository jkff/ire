package net.ire;

import net.ire.fa.BiDFA;
import net.ire.fa.DFA;
import net.ire.fa.Permutation;
import net.ire.fa.TransferFunction;
import org.junit.Test;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 31.07.2010 12:16:56
 */
public class LinearISTest {
    @Test
    public void testABC() {
        BitSet term = new BitSet(1);
        term.set(0);
        BitSet zero = new BitSet(1);

        DFA<Character> forward, backward;
        {
            DFA.State[] states = new DFA.State[4];
            states[0] = new DFA.State(zero);
            states[1] = new DFA.State(zero);
            states[2] = new DFA.State(zero);
            states[3] = new DFA.State(term);
            Map<Character, TransferFunction<Integer>> transfer = new HashMap<Character, TransferFunction<Integer>>();
            TransferFunction<Integer> transferA = new Permutation(new int[] {1, 0, 0, 3});
            TransferFunction<Integer> transferB = new Permutation(new int[] {0, 2, 0, 3});
            TransferFunction<Integer> transferC = new Permutation(new int[] {0, 0, 3, 3});
            transfer.put('a', transferA);
            transfer.put('b', transferB);
            transfer.put('c', transferC);
            forward = new DFA<Character>(states, transfer);
        }
        {
            DFA.State[] states = new DFA.State[4];
            states[0] = new DFA.State(zero);
            states[1] = new DFA.State(zero);
            states[2] = new DFA.State(zero);
            states[3] = new DFA.State(term);
            Map<Character, TransferFunction<Integer>> transfer = new HashMap<Character, TransferFunction<Integer>>();
            TransferFunction<Integer> transferA = new Permutation(new int[] {0, 0, 3, 3});
            TransferFunction<Integer> transferB = new Permutation(new int[] {0, 2, 0, 3});
            TransferFunction<Integer> transferC = new Permutation(new int[] {1, 0, 0, 3});
            transfer.put('a', transferA);
            transfer.put('b', transferB);
            transfer.put('c', transferC);
            backward = new DFA<Character>(states, transfer);
        }
        BiDFA<Character> bidfa = new BiDFA<Character>(forward, backward);
        LinearIS is = new LinearIS("cccabccccc", bidfa);
        for(Match m : is.getMatches()) {
            System.out.println(m.whichPattern() + " at " + m.startPos() + " with length " + m.length());
        }
    }
}
