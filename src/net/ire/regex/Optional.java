package net.ire.regex;

/**
 * Created on: 01.09.2010 21:59:35
 */
public class Optional implements RxNode {
    public final RxNode a;

    public Optional(RxNode a) {
        this.a = a;
    }
}
