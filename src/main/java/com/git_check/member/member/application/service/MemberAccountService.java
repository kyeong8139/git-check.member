package com.git_check.member.member.application.service;

import org.springframework.stereotype.Service;

import com.git_check.member.auth.application.port.out.MemberAccountPort;
import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;
import com.git_check.member.member.adapter.out.persistence.JpaMemberRepository;
import com.git_check.member.member.application.domain.Member;
import com.git_check.member.member.application.port.out.MemberPersistencePort;

@Service
public class MemberAccountService implements MemberAccountPort {

    private final MemberPersistencePort memberPersistencePort;

    public MemberAccountService(MemberPersistencePort memberPersistencePort) {
        this.memberPersistencePort = memberPersistencePort;
    }

    @Override
    public MemberInfo getMemberInfo(String socialLoginType, String socialLoginId) {
        Member member = memberPersistencePort.findMemberBySocialLoginTypeAndSocialLoginId(socialLoginType, socialLoginId);
        if (member == null) {
            return null;
        }

        return MemberInfo.from(member);
    }

    @Override
    public MemberInfo registerMember(MemberRegisterDto memberRegisterDto) {
        Member member = Member.builder()
            .name(memberRegisterDto.getName())
            .socialLoginType(memberRegisterDto.getSocialLoginType())
            .socialLoginId(memberRegisterDto.getSocialLoginId())
            .build();
        Member savedMember = memberPersistencePort.registerMember(member);
        return MemberInfo.from(savedMember);
    }
}
