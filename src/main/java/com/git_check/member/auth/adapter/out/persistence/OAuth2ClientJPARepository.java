package com.git_check.member.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OAuth2ClientJPARepository extends JpaRepository<OAuth2ClientEntity, Long> {
    Optional<OAuth2ClientEntity> findByProviderAndProviderId(String provider, String providerId);
}
