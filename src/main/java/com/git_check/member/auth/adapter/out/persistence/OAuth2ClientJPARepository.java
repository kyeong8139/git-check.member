package com.git_check.member.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface OAuth2ClientJPARepository extends JpaRepository<OAuth2ClientEntity, Long> {
    Optional<OAuth2ClientEntity> findByProviderAndPrincipalName(String provider, String principalName);

    @Modifying
    @Query("UPDATE OAuth2ClientEntity o SET o.refreshToken = :refreshToken, o.refreshTokenIssuedAt = :refreshTokenIssuedAt, o.deletedAt = :deletedAt WHERE o.id = :id")
    OAuth2ClientEntity updateState(long id, OAuth2ClientUpdate oAuth2ClientUpdate);
}