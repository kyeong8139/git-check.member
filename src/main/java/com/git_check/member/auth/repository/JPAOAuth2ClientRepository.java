package com.git_check.member.auth.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

@Repository
public interface JPAOAuth2ClientRepository extends JpaRepository<OAuth2Client, Long> {
    OAuth2Client findByProviderAndProviderId(String provider, String providerId);
    OAuth2Client updateDeletedAt(OAuth2Client oAuth2Client, LocalDateTime deletedAt);
}
