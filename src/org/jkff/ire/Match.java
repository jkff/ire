package org.jkff.ire;

/**
 * Created on: 22.07.2010 23:25:29
 */
public class Match {
    private int whichPattern;
    private int startPos;
    private int length;

    public Match(int whichPattern, int startPos, int length) {
        this.whichPattern = whichPattern;
        this.startPos = startPos;
        this.length = length;
    }

    public int whichPattern() {
        return whichPattern;
    }

    public int startPos() {
        return startPos;
    }

    public int length() {
        return length;
    }

    public String toString() {
        return "" + whichPattern + "@("+startPos+","+length+")";
    }
}
