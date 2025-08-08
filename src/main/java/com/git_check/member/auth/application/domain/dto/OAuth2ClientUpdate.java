package com.git_check.member.auth.application.domain.dto;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientUpdate {
    private OAuth2AccessToken accessToken;
    private OAuth2RefreshToken refreshToken;
    private Long deletedAt;
}
