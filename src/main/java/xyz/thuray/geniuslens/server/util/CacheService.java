package xyz.thuray.geniuslens.server.util;

public interface CacheService {
    Object get(String key);
    void set(String key, Object value);
    void set(String key, Object value, long expire);
    void expire(String key, long expire);
    void del(String key);
}
