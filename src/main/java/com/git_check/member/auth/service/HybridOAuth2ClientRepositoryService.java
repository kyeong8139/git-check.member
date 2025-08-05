package com.git_check.member.auth.service;

import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import com.git_check.member.auth.repository.JPAOAuth2ClientRepository;
import com.git_check.member.auth.repository.OAuth2ClientEntity;

import org.springframework.stereotype.Service;

@Service
public class HybridOAuth2ClientRepositoryService implements OAuth2AuthorizedClientService {

    private final JPAOAuth2ClientRepository jpaOAuth2ClientRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RedisOAuth2TokenService redisOAuth2TokenService;

    public HybridOAuth2ClientRepositoryService(JPAOAuth2ClientRepository jpaOAuth2ClientRepository, 
                                      RedisOAuth2TokenService redisOAuth2TokenService,
                                      ClientRegistrationRepository clientRegistrationRepository) {
        this.jpaOAuth2ClientRepository = jpaOAuth2ClientRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.redisOAuth2TokenService = redisOAuth2TokenService;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) return null;

        OAuth2ClientEntity oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client == null || oAuth2Client.getDeletedAt() != null) return null;
        System.out.println(oAuth2Client.toString());
        
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(oAuth2Client.getRefreshToken(), Instant.ofEpochMilli(oAuth2Client.getCreatedAt()));
        
        OAuth2AccessToken accessToken = redisOAuth2TokenService.getAccessToken(clientRegistrationId, principalName);
        
        T authorizedClient = (T) new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken);
        return authorizedClient;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String principalName = principal.getName();
        String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
        
        if (accessToken == null) return;
        
        OAuth2ClientEntity oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(registrationId, principalName);
        if (oAuth2Client == null) {
            oAuth2Client = OAuth2ClientEntity.builder()
                .provider(registrationId)
                .providerId(principalName)
                .refreshToken(refreshToken.getTokenValue())
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();
        }

        System.out.println(oAuth2Client.toString());
        jpaOAuth2ClientRepository.save(oAuth2Client);
        redisOAuth2TokenService.saveAccessToken(registrationId, principalName, accessToken);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        OAuth2ClientEntity oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client == null) {
            return;
        }

        oAuth2Client.setDeletedAt(Instant.now().toEpochMilli());
        jpaOAuth2ClientRepository.save(oAuth2Client);
        redisOAuth2TokenService.removeAccessToken(clientRegistrationId, principalName);
    }
}
