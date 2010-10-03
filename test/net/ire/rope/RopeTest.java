package net.ire.rope;

import net.ire.fa.Sequence;
import net.ire.util.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created on: 30.08.2010 23:04:07
 */
public class RopeTest {
    private static final Reducer<String> CONCAT = new Reducer<String>() {
        public String compose(String a, String b) {
            return a + b;
        }

        public String composeAll(Sequence<String> ss) {
            StringBuilder res = new StringBuilder();
            for(int i = 0; i < ss.length(); ++i) {
                res.append(ss.get(i));
            }
            return res.toString();
        }
    };
    private static final Function<Character, String> SINGLETON_STRING = new Function<Character, String>() {
        public String applyTo(Character c) {
            return "" + c;
        }
    };
    private static final Function2<Integer,Rope<String>,Integer> ADD_LENGTH = new Function2<Integer, Rope<String>, Integer>() {
        public Integer applyTo(Integer s, Rope<String> r) {
            return s + r.length();
        }
    };
    private static final Function2<Integer,Character,Integer> INCREMENT = new Function2<Integer, Character, Integer>() {
        public Integer applyTo(Integer s, Character character) {
            return s + 1;
        }
    };

    @Test
    public void testToFromString() {
        RopeFactory<String> f = new RopeFactory<String>(4, CONCAT, SINGLETON_STRING);
        assertEquals("abc", Rope.fromString(f, "abc").toString());
        assertEquals("abc", Rope.fromString(f, "abc").getSum());
        assertEquals("abcd", Rope.fromString(f, "abcd").toString());
        assertEquals("abcd", Rope.fromString(f, "abcd").getSum());
        assertEquals("abcde", Rope.fromString(f, "abcde").toString());
        assertEquals("abcde", Rope.fromString(f, "abcde").getSum());
        assertEquals("abcdefgh", Rope.fromString(f, "abcdefgh").toString());
        assertEquals("abcdefgh", Rope.fromString(f, "abcdefgh").getSum());
        assertEquals("abcdefghij", Rope.fromString(f, "abcdefghij").toString());
        assertEquals("abcdefghij", Rope.fromString(f, "abcdefghij").getSum());
        assertEquals("abcdefghijklmnopqrstuvwxyz", Rope.fromString(f, "abcdefghijklmnopqrstuvwxyz").toString());
        assertEquals("abcdefghijklmnopqrstuvwxyz", Rope.fromString(f, "abcdefghijklmnopqrstuvwxyz").getSum());
    }

    @Test
    public void testAppend() {
        RopeFactory<String> f = new RopeFactory<String>(4, CONCAT, SINGLETON_STRING);
        String s = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < s.length(); ++i) {
            Rope<String> part1 = Rope.fromString(f, s.substring(0, i));
            Rope<String> part2 = Rope.fromString(f, s.substring(i));
            assertEquals(s, part1.append(part2).toString());
            assertEquals(s, part1.append(part2).getSum());
        }

        for (int a = 0; a < s.length(); ++a) {
            for (int b = a; b < s.length(); ++b) {
                for (int c = b; c < s.length(); ++c) {
                    String[] parts = new String[]{
                            s.substring(0, a),
                            s.substring(a, b),
                            s.substring(b, c),
                            s.substring(c),
                    };
                    Rope[] ropes = new Rope[]{
                            Rope.fromString(f, parts[0]),
                            Rope.fromString(f, parts[1]),
                            Rope.fromString(f, parts[2]),
                            Rope.fromString(f, parts[3]),
                    };
                    assertEquals(s,
                            ropes[0].append(ropes[1]).append(ropes[2])
                                    .append(ropes[3]).toString());
                    assertEquals(s,
                            ropes[0].append(ropes[1]).append(ropes[2])
                                    .append(ropes[3]).getSum());
                }
            }
        }
    }

    @Test
    public void testSplitAfterRise() {
        RopeFactory<String> f = new RopeFactory<String>(4, CONCAT, SINGLETON_STRING);
        String s = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        Rope<String> r = Rope.fromString(f, s);

        for(int i = 0; i < s.length(); ++i) {
            Pair<Rope<String>,Rope<String>> p = r.splitAfterRise(0, ADD_LENGTH, INCREMENT, greaterThan(i-1));
            assertEquals(i, p.first.length());
            assertEquals(s, p.first.append(p.second).toString());
        }
        assertNull(r.splitAfterRise(0, ADD_LENGTH, INCREMENT, greaterThan(s.length())));
    }

    @Test
    public void testSplitAfterBackRise() {
        RopeFactory<String> f = new RopeFactory<String>(4, CONCAT, SINGLETON_STRING);
        String s = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        Rope<String> r = Rope.fromString(f, s);

        for(int i = 0; i < s.length(); ++i) {
            Pair<Rope<String>,Rope<String>> p = r.splitAfterBackRise(0, ADD_LENGTH, INCREMENT, greaterThan(i-1));
            assertEquals(i, p.second.length());
            assertEquals(s, p.first.append(p.second).toString());
        }
        assertNull(r.splitAfterBackRise(0, ADD_LENGTH, INCREMENT, greaterThan(s.length())));
    }

    private static Predicate<Integer> greaterThan(final int x) {
        return new Predicate<Integer>() {
            public boolean isTrueFor(Integer i) {
                return i > x;
            }
        };
    }
}
