package com.duriamuk.robartifact.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-11 19:23
 */
@Component
public class RedisUtils {
    @SuppressWarnings("rawtypes")
    private static RedisTemplate redisTemplate;

    @Autowired
    @SuppressWarnings("rawtypes")
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据模板批量删除key
     *
     * @param pattern
     */
    public static void deleteWithPattern(String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 根据key删除value
     *
     * @param key
     */
    public static void delete(String key) {
        if (hasKey(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断是否存在key
     *
     * @param key
     * @return
     */
    public static boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        Object result = operations.get(key);
        return result;
    }

    /**
     * 读取缓存List
     *
     * @param key
     * @return
     */
    public static List<String> getList(String key) {
        return objectToList(get(key));
    }

    /**
     * 写入缓存，不设置设置延迟和过期时间
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean set(String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，设置延迟和过期时间
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setWithTimeoutAndExpire(String key, Object value, long timeout, TimeUnit timeoutUnit, long expireTime, TimeUnit expireUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, timeoutUnit);
            redisTemplate.expire(key, expireTime, expireUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，设置默认延迟和默认过期时间
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setWithTimeoutAndExpire(String key, Object value, long timeout, long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, TimeUnit.SECONDS);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，设置延迟
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setWithTimeout(String key, Object value, long timeout, TimeUnit timeoutUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, timeoutUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，设置默认延迟
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setWithTimeout(String key, Object value, long timeout) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，设置过期时间
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setWithExpire(String key, Object value, long expireTime, TimeUnit expireUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, expireUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，设置默认过期时间
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setWithExpire(String key, Object value, long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<String> objectToList(Object object) {
        if (object instanceof List) {
            List<String> list = (List<String>) object;
            return list;
        }
        return null;
    }
}
