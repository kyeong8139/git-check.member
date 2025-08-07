package com.git_check.member.member.service;

import org.springframework.stereotype.Service;

import com.git_check.member.auth.application.port.out.LoadMemberInfo;
import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;

@Service
public class MemberAccountServiceImpl implements LoadMemberInfo {

    @Override
    public MemberInfo getMemberInfo(String socialLoginType, String socialLoginId) {
        // TODO: 데이터베이스에서 조회
        return new MemberInfo(0, "test", socialLoginType, socialLoginId);
    }

    @Override
    public MemberInfo registerMember(MemberRegisterDto memberRegisterDto) {
        // TODO: 데이터베이스에 저장
        return new MemberInfo(1L, "test", memberRegisterDto.getSocialLoginType(), memberRegisterDto.getSocialLoginId());
    }
}
