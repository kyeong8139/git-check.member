package com.git_check.member.auth.application.port.out;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;

public interface OAuth2ClientPort {
    OAuth2Client findByProviderAndPrincipalName(String provider, String principalName);
    OAuth2Client create(OAuth2ClientCreate oAuth2ClientCreate);
    void updateState(long id, OAuth2ClientUpdate oAuth2ClientUpdata);
    OAuth2AccessToken findLastAccessTokenByProviderAndPrincipalName(long clientId);
}   
