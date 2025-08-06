package com.git_check.member.member.service;

import org.springframework.stereotype.Service;

import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;

@Service
public class MemberAccountServiceImpl implements MemberAccountService {

    @Override
    public MemberInfo findMember(String socialLoginType, String socialLoginId) {
        // TODO: 데이터베이스에서 조회
        return new MemberInfo(0, "test", socialLoginType, socialLoginId);
    }

    @Override
    public MemberInfo registerAccount(MemberRegisterDto member) {
        // TODO: 데이터베이스에 저장
        return new MemberInfo(1L, "test", member.getSocialLoginType(), member.getSocialLoginId());
    }
}
