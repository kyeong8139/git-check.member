package com.git_check.member.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAccessTokenHistoryJPARepository extends JpaRepository<ClientAccessTokenEntity, Long> {
    @Query("SELECT ca FROM ClientAccessTokenEntity ca WHERE ca.client.id = :id ORDER BY ca.issuedAt DESC")
    Optional<ClientAccessTokenEntity> findLastAccessTokenByClientId(Long id);
}
