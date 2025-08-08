package com.git_check.member.auth.adapter.out.persistence;

import java.time.Instant;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "refresh_token_issued_at")
    private Long refreshTokenIssuedAt;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "deleted_at")
    private Long deletedAt;

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
        oAuth2ClientEntity.setProviderId(oAuth2ClientCreate.getProviderId());
        oAuth2ClientEntity.setRefreshToken(oAuth2ClientCreate.getRefreshToken());
        oAuth2ClientEntity.setRefreshTokenIssuedAt(oAuth2ClientCreate.getRefreshTokenIssuedAt());
        return oAuth2ClientEntity;
    }

    public static OAuth2ClientEntity from(OAuth2ClientUpdate oAuth2ClientUpdate) {
        OAuth2ClientEntity oAuth2ClientEntity = new OAuth2ClientEntity();
        oAuth2ClientEntity.setRefreshToken(oAuth2ClientUpdate.getRefreshToken());
        oAuth2ClientEntity.setRefreshTokenIssuedAt(oAuth2ClientUpdate.getRefreshTokenIssuedAt());
        oAuth2ClientEntity.setDeletedAt(oAuth2ClientUpdate.getDeletedAt());
        return oAuth2ClientEntity;
    }

    public OAuth2Client toModel() {
        return OAuth2Client.builder()
            .id(id)
            .provider(provider)
            .providerId(providerId)
            .refreshToken(refreshToken)
            .refreshTokenIssuedAt(refreshTokenIssuedAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .deletedAt(deletedAt)
            .build();
    }
}