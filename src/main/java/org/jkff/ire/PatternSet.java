package org.jkff.ire;

/**
 * Created on: 22.07.2010 23:24:31
 */
public interface PatternSet {
    IndexedString match(String s);

    IndexedString match(String s, int blockSize);
}
