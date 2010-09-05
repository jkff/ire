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

        public String toString() {
            return ".";
        }
    };

    public static CharacterClass oneOf(final String s) {
        return new OneOf(s);
    }

    private static class OneOf extends CharacterClass {
        private String s;

        public OneOf(String s) {
            this.s = s;
        }

        @Override
        public boolean acceptsChar(char c) {
            return s.indexOf(c) > -1;
        }

        public String toString() {
            return "[" + s + "]";
        }

        public boolean equals(Object other) {
            if(other == this) return true;
            if(other == null) return false;
            if(!(other instanceof OneOf)) return false;
            return s.equals(((OneOf)other).s);
        }
    }
}
