package net.ire.regex;

/**
 * Created on: 01.09.2010 21:59:15
 */
public class Sequence implements Node {
    public final Node a;
    public final Node b;

    public Sequence(Node a, Node b) {
        this.a = a;
        this.b = b;
    }
}
