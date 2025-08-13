package com.git_check.member.global.dto;

import com.git_check.member.member.application.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;  
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfo {
    long id;
    String name;
    String socialLoginType;
    String socialLoginId;

    public static MemberInfo from(Member member) {
        return MemberInfo.builder()
            .id(member.getId())
            .name(member.getName())
            .socialLoginType(member.getSocialLoginType())
            .socialLoginId(member.getSocialLoginId())
            .build();
    }
}
