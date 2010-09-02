package net.ire.regex;

/**
 * Created on: 01.09.2010 23:41:53
 */
public class OnceOrMore implements Node {
    public final Node a;

    public OnceOrMore(Node a) {
        this.a = a;
    }
}
