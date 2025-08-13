package com.git_check.member.member.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaMemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findBySocialLoginTypeAndSocialLoginId(String socialLoginType, String socialLoginId);
}
