package org.buaa.shortlink.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 邮箱code缓存
 */
@Component
public class MailCodeCache {

    private final Cache<String, String> codeCache;

    public MailCodeCache() {
        this.codeCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void put(String mail, String code) {
        codeCache.put(mail, code);
    }

    public String get(String mail) {
        return codeCache.getIfPresent(mail);
    }

}
