package com.git_check.member.auth.service;

import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.git_check.member.auth.exception.InvalidOAuth2ProviderException;
import com.git_check.member.auth.repository.JPAOAuth2ClientRepository;
import com.git_check.member.auth.repository.OAuth2ClientEntity;

@Service
@Transactional
public class HybridAuthorizedClientService implements OAuth2AuthorizedClientService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RedisService redisService;
    private final JPAOAuth2ClientRepository jpaOAuth2ClientRepository;
    private final String REDIS_KEY_PREFIX = "oauth2:access_token:";

    public HybridAuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository, RedisService redisService, JPAOAuth2ClientRepository jpaOAuth2ClientRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;   
        this.redisService = redisService;
        this.jpaOAuth2ClientRepository = jpaOAuth2ClientRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) {
            throw new InvalidOAuth2ProviderException();
        }

        OAuth2ClientEntity oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client == null || oAuth2Client.getDeletedAt() != null) {
            return null;
        }
        
        String redisKey = generateRedisKey(clientRegistrationId, principalName);
        OAuth2AccessToken accessToken = (OAuth2AccessToken) redisService.getFromRedis(redisKey);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(oAuth2Client.getRefreshToken(), Instant.ofEpochMilli(oAuth2Client.getCreatedAt()));
        return (T) new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String principalName = principal.getName();
        String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
        
        OAuth2ClientEntity oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(registrationId, principalName);
        if (oAuth2Client == null) {
            oAuth2Client = OAuth2ClientEntity.builder()
                .provider(registrationId)
                .providerId(principalName)
                .createdAt(Instant.now().toEpochMilli())
                .build();
        }
        
        oAuth2Client.setDeletedAt(null);
        oAuth2Client.setUpdatedAt(Instant.now().toEpochMilli());
        if (refreshToken != null) {
            oAuth2Client.setRefreshToken(refreshToken.getTokenValue());
            oAuth2Client.setRefreshTokenIssuedAt(refreshToken.getIssuedAt().toEpochMilli());
        }

        jpaOAuth2ClientRepository.save(oAuth2Client);
        redisService.saveToRedis(generateRedisKey(registrationId, principalName), accessToken, accessToken.getExpiresAt().toEpochMilli());
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        OAuth2ClientEntity oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client == null) {
            return;
        }

        oAuth2Client.setRefreshToken(null);
        oAuth2Client.setDeletedAt(Instant.now().toEpochMilli());
        oAuth2Client.setUpdatedAt(Instant.now().toEpochMilli());
        jpaOAuth2ClientRepository.save(oAuth2Client);
        redisService.removeFromRedis(generateRedisKey(clientRegistrationId, principalName));
    }

    private String generateRedisKey(String clientRegistrationId, String principalName) {
        return REDIS_KEY_PREFIX + clientRegistrationId + ":" + principalName;
    }
}
