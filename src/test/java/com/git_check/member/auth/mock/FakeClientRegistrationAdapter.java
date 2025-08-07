package com.git_check.member.auth.mock;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.HashMap;
import java.util.Map;

public class FakeClientRegistrationAdapter implements ClientRegistrationRepository {    
    private Map<String, ClientRegistration> clientRegistrationRepository = new HashMap<>();

    public FakeClientRegistrationAdapter(String registrationId) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId).build();
        clientRegistrationRepository.put(registrationId, clientRegistration);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return clientRegistrationRepository.getOrDefault(registrationId, null);
    }
}