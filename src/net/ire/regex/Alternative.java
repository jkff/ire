package net.ire.regex;

/**
 * Created on: 01.09.2010 23:42:24
 */
public class Alternative implements Node {
    public final Node a;
    public final Node b;

    public Alternative(Node a, Node b) {
        this.a = a;
        this.b = b;
    }
}
