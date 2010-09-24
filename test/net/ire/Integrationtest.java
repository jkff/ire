package net.ire;

import net.ire.regex.RegexCompiler;
import net.ire.regex.RegexParser;
import net.ire.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created on: 04.09.2010 18:10:51
 */
public class IntegrationTest {
    @Test
    public void test() {
        PatternSet pat = RegexCompiler.compile(Arrays.asList(RegexParser.parse(".*007.*")));
        IndexedString s1 = pat.match("as00haklsdjhfla00");
        IndexedString s2 = pat.match("7jhd7dsh007dsfa");
        System.out.println(s1.append(s2).getMatches());
    }

    @Test
    public void testPerformance() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 1000000; ++i) {
            sb.append('a');
        }
        sb.setCharAt(500000, '0');
        sb.setCharAt(500001, '0');
        sb.setCharAt(500002, '7');
        String s = sb.toString();

        // 256 block size: 1000 in 1680ms
        PatternSet pat = RegexCompiler.compile(Arrays.asList(RegexParser.parse(".*007.*")));

        long tStart = System.currentTimeMillis();
        IndexedString is = pat.match(s);

        System.out.println("******");
        is.append(is);
        System.out.println("******");

        System.out.println("Indexed in " + (System.currentTimeMillis() - tStart) + "ms");
        tStart = System.currentTimeMillis();
        long t0 = tStart;
        for(int i = 0; i < 1000000; ++i) {
            Pair<IndexedString,IndexedString> pair = is.splitBefore(i);
            if(i > 0 && i % 1000 == 0) {
                System.out.println("1000 done in " + (System.currentTimeMillis() - tStart) + " (" + (System.currentTimeMillis() - t0)+ ")");
                tStart = System.currentTimeMillis();
                System.out.println(
                        i + " " + pair.first.getMatches() + " / " +
                                pair.second.getMatches() + " / " + pair.first.append(pair.second).getMatches());
            }
        }

        System.out.println("-----");
        Pattern p = Pattern.compile(".*007.*");
        tStart = System.currentTimeMillis();
        for(int i = 0; i < 10000; ++i) {
            p.matcher(s).find();
            if(i > 0 && i % 1000 == 0) {
                System.out.println("1000 done in " + (System.currentTimeMillis() - tStart));
                tStart = System.currentTimeMillis();
            }
        }
    }

}
