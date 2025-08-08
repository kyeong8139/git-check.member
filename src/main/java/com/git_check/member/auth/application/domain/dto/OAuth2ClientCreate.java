package com.git_check.member.auth.application.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientCreate {
    private String provider;
    private String principalName;

    private String refreshToken;
    private Long refreshTokenIssuedAt;
}
