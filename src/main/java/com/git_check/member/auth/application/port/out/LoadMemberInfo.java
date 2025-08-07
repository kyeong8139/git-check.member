package com.git_check.member.auth.application.port.out;

import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;

public interface LoadMemberInfo {
    MemberInfo registerMember(MemberRegisterDto memberRegisterDto);
    MemberInfo getMemberInfo(String socialLoginType, String socialLoginId);
}