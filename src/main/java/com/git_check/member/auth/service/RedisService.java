package com.git_check.member.auth.service;

public interface RedisService {
    void saveToRedis(String key, Object value, long expirationTimeMillis);
    Object getFromRedis(String key);
    void removeFromRedis(String key);
    boolean hasKey(String key);
} 