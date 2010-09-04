package net.ire.util;

import java.util.*;

/**
 * Created on: 04.09.2010 10:52:27
 */
public class CollectionFactory {
    public static <T> List<T> newArrayList() {
        return new ArrayList<T>();
    }
    public static <T> Set<T> newHashSet() {
        return new HashSet<T>();
    }
    public static <K,V> Map<K,V> newHashMap() {
        return new HashMap<K,V>();
    }
}
