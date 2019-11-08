package com.stdwork_management.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    public static Object get(String key){
        Map<String, Object> map = THREAD_LOCAL.get();
        return map.get(key) == null ? null : map.get(key);
    }

    public static void put(String key, Object value){
        Map<String, Object> map = THREAD_LOCAL.get();
        Optional.ofNullable(map).orElse(map = new HashMap<>()).put(key, value);
        THREAD_LOCAL.set(map);
    }
}
