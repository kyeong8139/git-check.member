package com.git_check.member.auth.application.domain;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2Client {
    private Long id;
    private String provider;
    private String providerId;

    private String refreshToken;
    private Long refreshTokenIssuedAt;

    private Long createdAt;
    private Long updatedAt;
    private Long deletedAt;
}
