package com.stdwork_management.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Value("${spring.redis.prefix}")
    private String keyPrefix;

    @Autowired
    private RedisTemplate redisTemplate;

    public <T> void set(String key, T value, long times, TimeUnit timeUtnit) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(getKey(key), value, times, timeUtnit);
    }

    public <T> void set(String key, T value) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(getKey(key), value);
    }

    public <T> T get(String key) {
        if (redisTemplate.hasKey(getKey(key))) {
            ValueOperations operations = redisTemplate.opsForValue();
            return (T) operations.get(getKey(key));
        }
        return null;
    }

    public void delete(String key) {
        redisTemplate.delete(getKey(key));
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(getKey(key));
    }

    public String getKey(String key){
        return keyPrefix + key;
    }

}
