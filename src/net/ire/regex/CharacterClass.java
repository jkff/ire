package net.ire.regex;

/**
 * Created on: 01.09.2010 23:47:14
 */
public abstract class CharacterClass implements RxNode {
    public abstract boolean acceptsChar(char c);

    public static CharacterClass ANY_CHAR = new CharacterClass() {
        @Override
        public boolean acceptsChar(char c) {
            return true;
        }
    };

    public static CharacterClass oneOf(final String s) {
        return new CharacterClass() {
            @Override
            public boolean acceptsChar(char c) {
                return s.indexOf(c) != -1;
            }
        };
    }
}
