package net.ire.monoid;

import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static net.ire.monoid.PartiallyCommutativeProduct.multiply;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on: 09.09.2010 21:22:45
 */
public class PartiallyCommutativeProductTest {
    private static Monoid<Integer> SUM = new Monoid<Integer>() {
        public Integer unit() {
            return 0;
        }

        public Integer reduce(Integer a, Integer b) {
            return a + b;
        }

        public boolean doCommute(Integer a, Integer b) {
            return true;
        }
    };

    @Test
    public void testPower() {
        for(int i = 0; i < 100; ++i) {
            assertEquals(i, PartiallyCommutativeProduct.power(SUM, 1, i).intValue());
        }
    }

    @Test
    public void testMultiply() {
        Monoid<String> m = new Monoid<String>() {
            public String unit() {
                return "";
            }

            public String reduce(String a, String b) {
                return a + b;
            }

            public boolean doCommute(String a, String b) {
                if("ABC".contains(a) && "ABC".contains(b))
                    return true;
                if("B".equals(a) && "X".equals(b))
                    return true;
                if("X".equals(a) && "B".equals(b))
                    return true;
                return false;
            }
        };

        assertTrue(
                Arrays.asList("AAAXABBC", "AAAXACBB", "AAAXBACC", "AAAXBCCA", "AAAXCABB", "AAXCBBA")
                .contains(multiply(asList("A","B","A","A","X","C","B","A"), m)));
    }
}
