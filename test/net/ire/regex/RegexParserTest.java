package net.ire.regex;

import org.junit.Test;

/**
 * Created on: 04.09.2010 13:58:49
 */
public class RegexParserTest {
    @Test
    public void testParseDoesNotFail() {
        RegexParser.parse("");
        RegexParser.parse("a");
        RegexParser.parse("a*");
        RegexParser.parse("a+");
        RegexParser.parse("a?");
        RegexParser.parse("ab");
        RegexParser.parse("a+b");
        RegexParser.parse("a|b");
        RegexParser.parse("abc");
        RegexParser.parse("a|bc");
        RegexParser.parse("ab|c");
        RegexParser.parse("ab|bc");
        RegexParser.parse("[abc]");
        RegexParser.parse("[abc]+");
        RegexParser.parse("[abc]+|[a-z]?");
    }
}
