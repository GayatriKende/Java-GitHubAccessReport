
package com.example.githubreport.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheStore {

    private static final Map<String, Object> cache = new HashMap<>();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.get(key);
    }

    public static boolean contains(String key) {
        return cache.containsKey(key);
    }
}