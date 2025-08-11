package com.git_check.member.auth.adapter.out.persistence;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "client_access_token_history")
public class ClientAccessTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_type", nullable = false)
    private String tokenTypeValue;

    @Column(name = "token_value", nullable = false)
    private String tokenValue;

    @Column(name = "issued_at", nullable = false)
    private Long issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Long expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private OAuth2ClientEntity client;

    public static ClientAccessTokenEntity from(OAuth2AccessToken accessToken, OAuth2ClientEntity client) {
        ClientAccessTokenEntity clientAccessTokenEntity = new ClientAccessTokenEntity();
        clientAccessTokenEntity.setTokenTypeValue(accessToken.getTokenType().getValue());
        clientAccessTokenEntity.setTokenValue(accessToken.getTokenValue());
        clientAccessTokenEntity.setIssuedAt(accessToken.getIssuedAt().toEpochMilli());
        clientAccessTokenEntity.setExpiresAt(accessToken.getExpiresAt().toEpochMilli());
        clientAccessTokenEntity.setClient(client);
        return clientAccessTokenEntity;
    }

    public OAuth2AccessToken toModel() {
        TokenType tokenType = new TokenType(tokenTypeValue);
        return new OAuth2AccessToken(tokenType, tokenValue, Instant.ofEpochMilli(issuedAt), Instant.ofEpochMilli(expiresAt));
    }
}
