package xyz.thuray.geniuslens.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.thuray.geniuslens.server.util.CacheService;
import xyz.thuray.geniuslens.server.util.LocalCacheService;

@Component
public class CacheConfig {
    @Value("${cache.impl}")
    private String cacheImpl = "local";

    public CacheService getCacheService() {
        if (cacheImpl.equals("local")) {
            return new LocalCacheService();
//            case "redis":
//                return new RedisCacheService();
        }
        throw new IllegalArgumentException("Unknown cache impl: " + cacheImpl);
    }
}
