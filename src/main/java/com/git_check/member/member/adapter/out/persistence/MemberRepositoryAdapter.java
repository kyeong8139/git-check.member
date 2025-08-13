package com.git_check.member.member.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.git_check.member.member.application.domain.Member;
import com.git_check.member.member.application.port.out.MemberPersistencePort;

@Component
public class MemberRepositoryAdapter implements MemberPersistencePort {
    private final JpaMemberRepository jpaMemberRepository;

    public MemberRepositoryAdapter(JpaMemberRepository jpaMemberRepository) {
        this.jpaMemberRepository = jpaMemberRepository;
    }

    @Override
    public Member findMemberBySocialLoginTypeAndSocialLoginId(String socialLoginType, String socialLoginId) {
        MemberEntity memberEntity = jpaMemberRepository.findBySocialLoginTypeAndSocialLoginId(socialLoginType, socialLoginId).orElse(null);
        return memberEntity != null ? memberEntity.toModel() : null;
    }

    @Override
    public Member registerMember(Member member) {
        MemberEntity memberEntity = jpaMemberRepository.save(MemberEntity.from(member));
        return memberEntity.toModel();
    }
}