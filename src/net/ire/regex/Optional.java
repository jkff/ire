package net.ire.regex;

/**
 * Created on: 01.09.2010 21:59:35
 */
public class Optional implements Node {
    public final Node a;

    public Optional(Node a) {
        this.a = a;
    }
}
