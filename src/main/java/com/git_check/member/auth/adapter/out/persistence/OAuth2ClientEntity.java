package com.git_check.member.auth.adapter.out.persistence;

import java.time.Instant;
import java.util.List;

import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "oauth2_client")
public class OAuth2ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "principal_name", nullable = false)
    private String principalName;

    @Column(name = "refresh_token_value", nullable = false)
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at", nullable = false)
    private Long refreshTokenIssuedAt;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "deleted_at")
    private Long deletedAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientAccessTokenEntity> accessTokenHistory;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now().toEpochMilli();
        updatedAt = Instant.now().toEpochMilli();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now().toEpochMilli();
    }

    public static OAuth2ClientEntity from(OAuth2ClientCreate oAuth2ClientCreate) {
        OAuth2ClientEntity oAuth2ClientEntity = new OAuth2ClientEntity();
        oAuth2ClientEntity.setProvider(oAuth2ClientCreate.getProvider());
        oAuth2ClientEntity.setPrincipalName(oAuth2ClientCreate.getPrincipalName());
        oAuth2ClientEntity.setRefreshTokenValue(oAuth2ClientCreate.getRefreshToken().getTokenValue());
        oAuth2ClientEntity.setRefreshTokenIssuedAt(oAuth2ClientCreate.getRefreshToken().getIssuedAt().toEpochMilli());
        return oAuth2ClientEntity;
    }

    public static OAuth2ClientEntity from(OAuth2ClientUpdate oAuth2ClientUpdate) {
        OAuth2ClientEntity oAuth2ClientEntity = new OAuth2ClientEntity();
        oAuth2ClientEntity.setRefreshTokenValue(oAuth2ClientUpdate.getRefreshToken().getTokenValue());
        oAuth2ClientEntity.setRefreshTokenIssuedAt(oAuth2ClientUpdate.getRefreshToken().getIssuedAt().toEpochMilli());
        oAuth2ClientEntity.setDeletedAt(oAuth2ClientUpdate.getDeletedAt());
        return oAuth2ClientEntity;
    }

    public OAuth2Client toModel() {
        return OAuth2Client.builder()
            .id(id)
            .provider(provider)
            .principalName(principalName)
            .refreshToken(new OAuth2RefreshToken(refreshTokenValue, Instant.ofEpochMilli(refreshTokenIssuedAt)))
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .deletedAt(deletedAt)
            .build();
    }
}