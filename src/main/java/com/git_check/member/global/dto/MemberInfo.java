package com.git_check.member.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfo {
    long id;
    String name;
    String socialLoginType;
    String socialLoginId;
}
