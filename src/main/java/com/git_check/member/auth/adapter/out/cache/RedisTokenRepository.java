package com.git_check.member.auth.adapter.out.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.git_check.member.auth.application.port.out.LoadToken;

@Service
public class RedisTokenRepository implements LoadToken {

    private final RedisTemplate<String, Object> redisTemplate;
    public RedisTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, Object value, long expirationTimeMillis) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(expirationTimeMillis));
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
} 