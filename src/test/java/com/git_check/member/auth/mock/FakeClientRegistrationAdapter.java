package com.git_check.member.auth.mock;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.HashMap;
import java.util.Map;

public class FakeClientRegistrationAdapter implements ClientRegistrationRepository {    
    private Map<String, ClientRegistration> clientRegistrationRepository = new HashMap<>();

    public FakeClientRegistrationAdapter(String registrationId) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
            .clientId("clientId")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("redirectUri")
            .authorizationUri("authorizationUri")
            .tokenUri("tokenUri")
            .build();
        clientRegistrationRepository.put(registrationId, clientRegistration);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return clientRegistrationRepository.getOrDefault(registrationId, null);
    }
}