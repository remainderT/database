package org.buaa.shortlink.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Token缓存
 */
@Component
public class UserTokenCache {

    private final Cache<String, String> tokenCache;

    public UserTokenCache() {
        this.tokenCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    public void put(String username, String token) {
        tokenCache.put(username, token);
    }

    public String get(String username) {
        return tokenCache.getIfPresent(username);
    }

    public void evict(String username) {
        tokenCache.invalidate(username);
    }

    public void refresh(String username) {
        String newToken = get(username);
        tokenCache.put(username, newToken);
    }

}
