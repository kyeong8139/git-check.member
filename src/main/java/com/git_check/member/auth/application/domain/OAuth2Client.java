package com.git_check.member.auth.application.domain;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

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
    private String principalName;

    private OAuth2AccessToken accessToken;
    private OAuth2RefreshToken refreshToken;

    private Long createdAt;
    private Long updatedAt;
    private Long deletedAt;
}
