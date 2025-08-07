package com.git_check.member.auth.mock;

import com.git_check.member.auth.application.port.out.CachePort;

import java.util.Map;
import java.util.HashMap;

public class FakeCacheAdapter implements CachePort {

    private Map<String, MockCacheEntry> cacheRepository = new HashMap<>();
    private long currentTimeMills;

    public FakeCacheAdapter(long currentTimeMills) {
        this.currentTimeMills = currentTimeMills;
    }

    @Override
    public void save(String key, Object value, long expirationTimeMillis) {
        if (expirationTimeMillis <= currentTimeMills) return;
        MockCacheEntry mockCacheEntry = MockCacheEntry.builder()
            .value(value)
            .expiresAt(expirationTimeMillis)
            .build();

        cacheRepository.put(key, mockCacheEntry);
    }

    @Override
    public Object get(String key) {
        MockCacheEntry mockCacheEntry = cacheRepository.get(key);
        if (isExist(mockCacheEntry)) {
            return mockCacheEntry.getValue();
        }
        return null;
    }

    @Override
    public void remove(String key) {
        cacheRepository.remove(key);
    }

    @Override
    public boolean hasKey(String key) {
        MockCacheEntry mockCacheEntry = cacheRepository.get(key);
        return isExist(mockCacheEntry);
    }

    private boolean isExist(MockCacheEntry mockCacheEntry) {
        return mockCacheEntry != null && mockCacheEntry.getExpiresAt() > currentTimeMills;
    }
}
