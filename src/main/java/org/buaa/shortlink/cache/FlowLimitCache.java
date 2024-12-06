package org.buaa.shortlink.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FlowLimitCache {

    private final Cache<String , AtomicInteger> flowCache;

    public FlowLimitCache() {
        this.flowCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build();
    }

    public int incrAndGet(String username) {
        AtomicInteger requestCount = flowCache.getIfPresent(username);
        if (requestCount != null) {
            return requestCount.addAndGet(1);
        } else {
            flowCache.put(username, new AtomicInteger(1));
            return 1;
        }
    }
}
