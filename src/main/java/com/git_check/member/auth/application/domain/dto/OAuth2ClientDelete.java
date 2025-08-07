package com.git_check.member.auth.application.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientDelete {
    private String refreshToken;
    private Long refreshTokenIssuedAt;

    private Long deletedAt;
}
