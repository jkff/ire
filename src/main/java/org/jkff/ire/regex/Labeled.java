package org.jkff.ire.regex;

/**
 * Created on: 04.09.2010 12:14:35
 */
public class Labeled implements RxNode {
    public final RxNode a;
    public final int patternId;

    public Labeled(RxNode a, int patternId) {
        this.a = a;
        this.patternId = patternId;
    }
}
