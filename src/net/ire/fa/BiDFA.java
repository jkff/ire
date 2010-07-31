package net.ire.fa;

/**
 * Created on: 25.07.2010 13:34:11
 */
public class BiDFA<C> {
    private DFA<C> forward;
    private DFA<C> backward;

    public BiDFA(DFA<C> forward, DFA<C> backward) {
        this.forward = forward;
        this.backward = backward;
    }

    public DFA<C> getForward() {
        return forward;
    }

    public DFA<C> getBackward() {
        return backward;
    }
}
