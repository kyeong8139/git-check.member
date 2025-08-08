package com.git_check.member.auth.application.port.out;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;

public interface OAuth2ClientPort {
    OAuth2Client findByProviderAndProviderId(String provider, String providerId);
    OAuth2Client create(OAuth2ClientCreate oAuth2ClientCreate);
    OAuth2Client updateState(long id, OAuth2ClientUpdate oAuth2ClientUpdata);
}   
