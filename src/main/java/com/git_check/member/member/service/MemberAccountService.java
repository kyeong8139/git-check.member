package com.git_check.member.member.service;

import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;

public interface MemberAccountService {
    MemberInfo findMember(String socialLoginType, String socialLoginId);
    MemberInfo registerAccount(MemberRegisterDto member);
}