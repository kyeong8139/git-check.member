package com.git_check.member.auth.application.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.git_check.member.auth.application.domain.OidcPrincipal;
import com.git_check.member.auth.application.domain.dto.JwtToken;
import com.git_check.member.auth.application.port.in.JwtTokenPort;
import com.git_check.member.auth.application.port.out.CachePort;

import io.jsonwebtoken.Claims;  
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService implements JwtTokenPort{
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
        this.accessTokenSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.cachePort = cachePort;
    }

    @Override
    public String createAccessToken(OidcPrincipal OidcPrincipal) {
        String versionKey = generateVersionKey(String.valueOf(OidcPrincipal.getMemberId()));
        if (!cachePort.hasKey(versionKey)) {
            cachePort.save(versionKey, "1");
        }
        int version = (Integer) cachePort.get(versionKey);
        
        java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);
        String accessToken = Jwts.builder()
            .claim("id",OidcPrincipal.getMemberId())
            .claim("name", OidcPrincipal.getMemberName())
            .claim("version", version)
            .issuedAt(new java.util.Date())
            .expiration(expirationDate)
            .signWith(accessTokenSecretKey)
        .compact();

        return accessToken;
    }

    @Override
    public String createRefreshToken(OidcPrincipal OidcPrincipal) {
        String versionKey = generateVersionKey(String.valueOf(OidcPrincipal.getMemberId()));
        int version = 1;
        if (cachePort.hasKey(versionKey)) {
            version = (Integer) cachePort.get(versionKey);
        }

        java.util.Date now = new java.util.Date();
        java.util.Date expirationDate = new java.util.Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);
        String refreshToken = Jwts.builder()
            .claim("id",OidcPrincipal.getMemberId())
            .claim("name", OidcPrincipal.getMemberName())
            .claim("version", version)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(refreshTokenSecretKey)
        .compact();

        return refreshToken;    
    }

    @Override
    public JwtToken reissueToken(String refreshToken) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                .verifyWith(refreshTokenSecretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();            
        } catch(ExpiredJwtException | SignatureException e) {
            return null;
        }

        long id = claims.get("id", Long.class);
        String name = claims.get("name", String.class);
        int version = claims.get("version", Integer.class);

        String versionKey = generateVersionKey(String.valueOf(id));
        if (!cachePort.hasKey(versionKey) || version != ((Integer) cachePort.get(versionKey))) {
            return null;
        }

        java.util.Date now = new java.util.Date();
        java.util.Date accessTokenExpirationDate = new java.util.Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);
        java.util.Date refreshTokenExpirationDate = new java.util.Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        String newAccessToken = Jwts.builder()
            .claim("id", id)
            .claim("name", name) 
            .claim("version", version)
            .issuedAt(now)
            .expiration(accessTokenExpirationDate)
            .signWith(accessTokenSecretKey)
            .compact();

        String newRefreshToken = Jwts.builder()
            .claim("id", id)
            .claim("name", name)
            .claim("version", version)
            .issuedAt(now)
            .expiration(refreshTokenExpirationDate)
            .signWith(refreshTokenSecretKey)
            .compact();

        cachePort.save(versionKey, String.valueOf(version));

        return JwtToken.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .build();
    }

    @Override
    public void logout(String accessToken) {
        Claims claims = Jwts.parser()
            .verifyWith(accessTokenSecretKey)
            .build()
            .parseSignedClaims(accessToken)
            .getPayload();

        long id = claims.get("id", Long.class);
        int version = claims.get("version", Integer.class);
        String versionKey = generateVersionKey(String.valueOf(id));
        cachePort.remove(versionKey);
        cachePort.save(versionKey, version + 1);
    }

    private String generateVersionKey(String memberId) {
        return "jwt:version:" + memberId;
    }
}            