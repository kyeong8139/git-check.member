package com.git_check.member.member.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    Long id;
    String name;
    String socialLoginType;
    String socialLoginId;
}
