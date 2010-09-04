package net.ire.regex;

/**
 * Created on: 01.09.2010 21:59:15
 */
public class Sequence implements RxNode {
    public final RxNode a;
    public final RxNode b;

    public Sequence(RxNode a, RxNode b) {
        this.a = a;
        this.b = b;
    }
}
