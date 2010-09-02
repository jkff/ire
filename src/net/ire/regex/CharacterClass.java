package net.ire.regex;

/**
 * Created on: 01.09.2010 23:47:14
 */
public abstract class CharacterClass implements Node {
    public abstract boolean acceptsChar(char c); 
}
