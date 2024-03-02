package xyz.thuray.geniuslens.server.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class LockUtil {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 锁的过期时间，单位毫秒
    private static final long LOCK_EXPIRE_TIME = 3600;
    // key的公共前缀
    private static final String LOCK_PREFIX = "lock:";

    // 获取锁的 Lua 脚本
    private static final String LOCK_SCRIPT =
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('expire', KEYS[1], ARGV[2]); " +
                    "return true; " +
                    "else " +
                    "return false; " +
                    "end";

    // 释放锁的 Lua 脚本
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "redis.call('del', KEYS[1]); " +
                    "return true; " +
                    "else return false; " +
                    "end";

    // 获取分布式锁
    public boolean lock(String key, String value) {
        String[] keys = {key};
        String[] args = {value, String.valueOf(LOCK_EXPIRE_TIME)};
        log.info("LockUtil: 获取锁参数：key={}, value={}, expireTime={}", key, value, LOCK_EXPIRE_TIME);
        RedisScript<Boolean> script = new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class);
        Boolean result = redisTemplate.execute(script, Arrays.asList(keys), args);
        log.info("LockUtil: 获取锁结果：{}", result);
        return result != null && result;
    }

    // 释放分布式锁
    public boolean unlock(String key, String value) {
        String[] keys = {key};
        String[] args = {value};
        RedisScript<Boolean> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Boolean.class);
        Boolean result = redisTemplate.execute(script, Arrays.asList(keys), args);
        log.info("LockUtil: 释放锁结果：{}", result);
        return result != null && result;
    }
}
