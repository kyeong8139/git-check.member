package com.git_check.member.auth.application.port.out;

import com.git_check.member.global.dto.MemberInfo;

public interface MemberAccountPort {
    MemberInfo getMemberInfo(String name, String socialLoginType, String socialLoginId);
}