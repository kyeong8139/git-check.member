package com.git_check.member.auth.adapter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;

@Configuration
public class OAuth2LoginComponentConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2LoginComponentConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
    
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
            new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
            costomizer -> costomizer.additionalParameters(params -> {
                params.put("prompt", "select_account");
            }));

        return authorizationRequestResolver;
    }
}
