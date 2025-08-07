package com.git_check.member.auth.application.port.out;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientDelete;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;

public interface SaveOAuth2Client {
    OAuth2Client findByProviderAndProviderId(String provider, String providerId);
    void create(OAuth2ClientCreate oAuth2ClientCreate);
    void update(OAuth2ClientUpdate oAuth2ClientUpdata);
    void delete(OAuth2ClientDelete oAuth2ClientDelete);
}   
