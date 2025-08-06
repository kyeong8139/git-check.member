package com.git_check.member.auth.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.git_check.member.auth.OidcPrincipalDetail;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService{
    private final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    private final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
    private final SecretKey accessTokenSecretKey;
    private final SecretKey refreshTokenSecretKey;
    private final RedisService redisService;

    public JwtServiceImpl(
            @Value("${jwt.access-token-secret}") String accessSecret,
            @Value("${jwt.refresh-token-secret}") String refreshSecret,
            RedisService redisService
    ) {
        this.redisService = redisService;
        this.accessTokenSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(OidcPrincipalDetail OidcPrincipal) {
        java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);
        String accessToken = Jwts.builder()
            .subject(String.valueOf(OidcPrincipal.getMemberId()))
            .content("name", OidcPrincipal.getMemberName())
            .issuedAt(new java.util.Date())
            .expiration(expirationDate)
            .signWith(accessTokenSecretKey)
        .compact();

        String redisKey = generateAccessTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        redisService.saveToRedis(redisKey, accessToken, expirationDate.getTime());
        return accessToken;
    }

    @Override
    public String createRefreshToken(OidcPrincipalDetail OidcPrincipal) {
        java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);
        String refreshToken = Jwts.builder()
            .subject(String.valueOf(OidcPrincipal.getMemberId()))
            .issuedAt(new java.util.Date())
            .expiration(new java.util.Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(refreshTokenSecretKey)
        .compact();

        String redisKey = generateRefreshTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        redisService.saveToRedis(redisKey, refreshToken, expirationDate.getTime());
        return refreshToken;    
    }

    @Override
    public void expireAllToken(OidcPrincipalDetail OidcPrincipal) {
        String refreshTokenKey = generateRefreshTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        String accessTokenKey = generateAccessTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        redisService.removeFromRedis(refreshTokenKey);
        redisService.removeFromRedis(accessTokenKey);
    }

    private String generateRefreshTokenKey(String memberId) {
        return "jwt:refresh_token:" + memberId;
    }

    private String generateAccessTokenKey(String memberId) {
        return "jwt:access_token:" + memberId;
    }
}            