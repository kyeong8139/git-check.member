package com.git_check.member.member.application.service;

import org.springframework.stereotype.Service;

import com.git_check.member.auth.application.port.out.MemberAccountPort;
import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.member.application.domain.Member;
import com.git_check.member.member.application.port.out.MemberPersistencePort;

@Service
public class MemberAccountService implements MemberAccountPort {

    private final MemberPersistencePort memberPersistencePort;

    public MemberAccountService(MemberPersistencePort memberPersistencePort) {
        this.memberPersistencePort = memberPersistencePort;
    }

    @Override
    public MemberInfo getMemberInfo(String name, String socialLoginType, String socialLoginId) {
        Member member = memberPersistencePort.findMemberBySocialLoginTypeAndSocialLoginId(socialLoginType, socialLoginId);
        if (member == null) {
            return MemberInfo.from(registerMember(name, socialLoginType, socialLoginId));
        }

        return MemberInfo.from(member);
    }

    private Member registerMember(String name, String socialLoginType, String socialLoginId) {
        Member member = Member.builder()
            .name(name)
            .socialLoginType(socialLoginType)
            .socialLoginId(socialLoginId)
            .build();
        return memberPersistencePort.registerMember(member);
    }
}
