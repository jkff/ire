package net.ire.fa;

import net.ire.util.Reducer;

/**
 * Created on: 22.07.2010 23:54:27
 */
public abstract class DFA<C, S extends State> {
    private TransferTable<C,S> transfer;
    private S initialState;
    private Reducer<TransferFunction<S>> transferFunctionsReducer;

    public DFA(TransferTable<C, S> transfer, S initialState,
               Reducer<TransferFunction<S>> transferFunctionsReducer)
    {
        this.transfer = transfer;
        this.initialState = initialState;
        this.transferFunctionsReducer = transferFunctionsReducer;
    }

    public S getInitialState() {
        return initialState;
    }

    public TransferFunction<S> transfer(C token) {
        return transfer.forToken(token);
    }

    public Reducer<TransferFunction<S>> getTransferFunctionsReducer() {
        return transferFunctionsReducer;
    }

    public abstract S resetTerminatedPattern(S state, int pattern);
}
