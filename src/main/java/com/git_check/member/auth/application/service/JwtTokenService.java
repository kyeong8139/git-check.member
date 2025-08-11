package com.git_check.member.auth.application.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.git_check.member.auth.application.domain.OidcPrincipal;
import com.git_check.member.auth.application.port.in.ProvideJwtToken;
import com.git_check.member.auth.application.port.out.CachePort;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService implements ProvideJwtToken{
    private final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    private final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
    private final SecretKey accessTokenSecretKey;
    private final SecretKey refreshTokenSecretKey;
    private final CachePort cachePort;

    public JwtTokenService(
            @Value("${jwt.access-token-secret}") String accessSecret,
            @Value("${jwt.refresh-token-secret}") String refreshSecret,
            CachePort cachePort
    ) {
        this.cachePort = cachePort;
        this.accessTokenSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(OidcPrincipal OidcPrincipal) {
        java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);
        String accessToken = Jwts.builder()
            .subject(String.valueOf(OidcPrincipal.getMemberId()))
            .claim("name", OidcPrincipal.getMemberName())
            .issuedAt(new java.util.Date())
            .expiration(expirationDate)
            .signWith(accessTokenSecretKey)
        .compact();

        String redisKey = generateAccessTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        cachePort.save(redisKey, accessToken, expirationDate.getTime());
        return accessToken;
    }

    @Override
    public String createRefreshToken(OidcPrincipal OidcPrincipal) {
        java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);
        String refreshToken = Jwts.builder()
            .subject(String.valueOf(OidcPrincipal.getMemberId()))
            .issuedAt(new java.util.Date())
            .expiration(new java.util.Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(refreshTokenSecretKey)
        .compact();

        String redisKey = generateRefreshTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        cachePort.save(redisKey, refreshToken, expirationDate.getTime());
        return refreshToken;    
    }

    @Override
    public void expireAllToken(OidcPrincipal OidcPrincipal) {
        String refreshTokenKey = generateRefreshTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        String accessTokenKey = generateAccessTokenKey(String.valueOf(OidcPrincipal.getMemberId()));
        cachePort.remove(refreshTokenKey);
        cachePort.remove(accessTokenKey);
    }

    private String generateRefreshTokenKey(String memberId) {
        return "jwt:refresh_token:" + memberId;
    }

    private String generateAccessTokenKey(String memberId) {
        return "jwt:access_token:" + memberId;
    }
}            