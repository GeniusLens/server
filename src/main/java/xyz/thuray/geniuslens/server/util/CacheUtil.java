package xyz.thuray.geniuslens.server.util;

import org.springframework.stereotype.Component;
import xyz.thuray.geniuslens.server.config.CacheConfig;

@Component
public class CacheUtil {
    private final CacheService cacheService;

    public CacheUtil(CacheConfig cacheConfig) {
        this.cacheService = cacheConfig.getCacheService();
    }

    public Object get(String key) {
        return cacheService.get(key);
    }

    public void set(String key, Object value) {
        cacheService.set(key, value);
    }

    public void set(String key, Object value, long expire) {
        cacheService.set(key, value, expire);
    }

    public void expire(String key, long expire) {
        cacheService.expire(key, expire);
    }

    public void del(String key) {
        cacheService.del(key);
    }
}
