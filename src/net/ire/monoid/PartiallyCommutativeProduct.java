package net.ire.monoid;

import java.util.Iterator;
import java.util.Map;

import static net.ire.util.CollectionFactory.newHashMap;

/**
 * Created on: 09.09.2010 21:12:50
 */
public class PartiallyCommutativeProduct {
    public static <T> T multiply(Iterable<T> ts, Monoid<T> m) {
        T res = m.unit();
        Map<T, Integer> activeBag = newHashMap();

        for(T t : ts) {
            Iterator<Map.Entry<T, Integer>> it = activeBag.entrySet().iterator();
            boolean found = false;
            while(it.hasNext()) {
                Map.Entry<T, Integer> entry = it.next();
                T active = entry.getKey();
                int num = entry.getValue();

                if (!m.doCommute(t, active)) {
                    it.remove();
                    res = m.reduce(res, power(m, active, num));
                } else if(t.equals(active)) {
                    entry.setValue(num+1);
                    found = true;
                }
            }
            if(!found)
                activeBag.put(t, 1);
        }
        for(Map.Entry<T,Integer> entry : activeBag.entrySet()) {
            res = m.reduce(res, power(m, entry.getKey(), entry.getValue()));
        }

        return res;
    }

    public static <T> T power(Monoid<T> m, T t, int num) {
        T res = m.unit();
        T powerOfTwo = t;
        while(num > 0) {
            if(num%2 == 1) {
                res = m.reduce(res, powerOfTwo);
            }
            num >>= 1;
            powerOfTwo = m.reduce(powerOfTwo, powerOfTwo);
        }

        return res;
    }
}
