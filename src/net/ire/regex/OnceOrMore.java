package net.ire.regex;

/**
 * Created on: 01.09.2010 23:41:53
 */
public class OnceOrMore implements RxNode {
    public final RxNode a;

    public OnceOrMore(RxNode a) {
        this.a = a;
    }
}
