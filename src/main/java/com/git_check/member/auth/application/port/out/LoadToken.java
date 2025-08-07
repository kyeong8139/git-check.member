package com.git_check.member.auth.application.port.out;

public interface LoadToken {
    void save(String key, Object value, long expirationTimeMillis);
    Object get(String key);
    void remove(String key);
    boolean hasKey(String key);
} 