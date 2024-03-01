package xyz.thuray.geniuslens.server.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCacheService implements CacheService {
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    @Override
    public Object get(String key) {
        return CACHE.get(key);
    }

    @Override
    public void set(String key, Object value) {
        CACHE.put(key, value);
    }

    @Override
    public void set(String key, Object value, long expire) {
        CACHE.put(key, value);
    }

    @Override
    public void expire(String key, long expire) {
        // do nothing
    }

    @Override
    public void del(String key) {
        CACHE.remove(key);
    }
}
