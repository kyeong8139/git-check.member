package com.git_check.member.member.application.port.out;

import com.git_check.member.member.application.domain.Member;

public interface MemberPersistencePort {
    Member findMemberBySocialLoginTypeAndSocialLoginId(String socialLoginType, String socialLoginId);
    Member registerMember(Member member);
}
