package net.ire.util;

import java.util.*;

/**
 * Created on: 04.09.2010 10:52:27
 */
public class CollectionFactory {
    public static <T> List<T> newArrayList() {
        return new ArrayList<T>();
    }
    public static <T> Set<T> newLinkedHashSet() {
        return new LinkedHashSet<T>();
    }
    public static <K,V> Map<K,V> newLinkedHashMap() {
        return new LinkedHashMap<K,V>();
    }
}
