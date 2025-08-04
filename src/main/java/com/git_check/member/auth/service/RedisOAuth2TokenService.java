package com.git_check.member.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RedisOAuth2TokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_KEY_PREFIX = "oauth2:access_token:";
    private static final Duration TOKEN_EXPIRY = Duration.ofHours(1); // 1시간

    public RedisOAuth2TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveAccessToken(String provider, String principalName, String accessToken) {
        String key = generateKey(provider, principalName);
        redisTemplate.opsForValue().set(key, accessToken, TOKEN_EXPIRY);
    }

    public String getAccessToken(String provider, String principalName) {
        String key = generateKey(provider, principalName);
        return redisTemplate.opsForValue().get(key);
    }

    public void removeAccessToken(String provider, String principalName) {
        String key = generateKey(provider, principalName);
        redisTemplate.delete(key);
    }

    public boolean hasAccessToken(String provider, String principalName) {
        String key = generateKey(provider, principalName);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String generateKey(String provider, String principalName) {
        return TOKEN_KEY_PREFIX + provider + ":" + principalName;
    }
} 