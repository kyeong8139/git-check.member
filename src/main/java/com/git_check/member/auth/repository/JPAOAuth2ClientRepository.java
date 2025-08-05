package com.git_check.member.auth.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JPAOAuth2ClientRepository extends JpaRepository<OAuth2ClientEntity, Long> {
    OAuth2ClientEntity findByProviderAndProviderId(String provider, String providerId);
}
