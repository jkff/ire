package net.ire;

import net.ire.fa.*;
import org.junit.Test;

import java.util.BitSet;

/**
 * Created on: 31.07.2010 12:16:56
 */
public class LinearISTest {
    @Test
    public void testABC() {
        BitSet term = new BitSet(1);
        term.set(0);
        BitSet zero = new BitSet(1);

        DFA<Character,IntState> forward, backward;
        {
            IntState[] states = new IntState[4];
            states[0] = new IntState(0, zero);
            states[1] = new IntState(1, zero);
            states[2] = new IntState(2, zero);
            states[3] = new IntState(3, term);
            final TransferFunction<IntState> transferA = new IntTable(states, new int[] {1, 0, 0, 3});
            final TransferFunction<IntState> transferB = new IntTable(states, new int[] {0, 2, 0, 3});
            final TransferFunction<IntState> transferC = new IntTable(states, new int[] {0, 0, 3, 3});
            final TransferFunction<IntState> transferX = new IntTable(states, new int[] {0, 0, 0, 3});
            TransferTable<Character, IntState> transfer = new TransferTable<Character, IntState>() {
                public TransferFunction<IntState> forToken(Character token) {
                    if(token == 'a') {
                        return transferA;
                    } else if(token == 'b') {
                        return transferB;
                    } else if(token == 'c') {
                        return transferC;
                    } else {
                        return transferX;
                    }
                }
            };
            forward = new DFA<Character, IntState>(transfer, states[0]);
        }
        {
            IntState[] states = new IntState[4];
            states[0] = new IntState(0, zero);
            states[1] = new IntState(1, zero);
            states[2] = new IntState(2, zero);
            states[3] = new IntState(3, term);
            final TransferFunction<IntState> transferA = new IntTable(states, new int[] {1, 0, 0, 3});
            final TransferFunction<IntState> transferB = new IntTable(states, new int[] {0, 2, 0, 3});
            final TransferFunction<IntState> transferC = new IntTable(states, new int[] {0, 0, 3, 3});
            final TransferFunction<IntState> transferX = new IntTable(states, new int[] {0, 0, 0, 3});
            TransferTable<Character, IntState> transfer = new TransferTable<Character, IntState>() {
                public TransferFunction<IntState> forToken(Character token) {
                    if(token == 'a') {
                        return transferA;
                    } else if(token == 'b') {
                        return transferB;
                    } else if(token == 'c') {
                        return transferC;
                    } else {
                        return transferX;
                    }
                }
            };
            backward = new DFA<Character, IntState>(transfer, states[0]);
        }
        BiDFA<Character,IntState> bidfa = new BiDFA<Character, IntState>(forward, backward);
        LinearIS<?> is = new LinearIS<IntState>("cccabccccc", bidfa);
        for(Match m : is.getMatches()) {
            System.out.println(m.whichPattern() + " at " + m.startPos() + " with length " + m.length());
        }
    }
}
