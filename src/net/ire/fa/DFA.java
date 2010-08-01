package net.ire.fa;

/**
 * Created on: 22.07.2010 23:54:27
 */
public class DFA<C, S extends State> {
    private TransferTable<C,S> transfer;
    private S initialState;

    public DFA(TransferTable<C,S> transfer, S initialState) {
        this.transfer = transfer;
        this.initialState = initialState;
    }

    public S getInitialState() {
        return initialState;
    }

    public TransferFunction<S> transfer(C token) {
        return transfer.forToken(token);
    }
}
