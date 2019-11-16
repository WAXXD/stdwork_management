package com.stdwork_management.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal(){
        protected  Map<String, Object> initialValue(){
            return new HashMap<>();
        }
    };



    public static Object get(String key){
        Map<String, Object> map = THREAD_LOCAL.get();
        return map.get(key);
    }

    public static void put(String key, Object value){
        Map<String, Object> map = THREAD_LOCAL.get();
        map.put(key, value);
    }
}
