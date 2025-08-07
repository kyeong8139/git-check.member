package com.git_check.member.auth.application.service;

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

import com.git_check.member.auth.adapter.config.exception.InvalidOAuth2ProviderException;
import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;
import com.git_check.member.auth.application.port.out.OAuth2ClientPort;
import com.git_check.member.auth.application.port.out.CachePort;

@Service
@Transactional
public class HybridOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2ClientPort oAuth2ClientPort;
    private final CachePort cachePort;  
    private final String REDIS_KEY_PREFIX = "oauth2:access_token:";

    public HybridOAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository, CachePort cachePort, OAuth2ClientPort oAuth2ClientPort) {
        this.clientRegistrationRepository = clientRegistrationRepository;   
        this.oAuth2ClientPort = oAuth2ClientPort;
        this.cachePort = cachePort;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) {
            throw new InvalidOAuth2ProviderException();
        }

        OAuth2Client oAuth2Client = oAuth2ClientPort.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client == null || oAuth2Client.getDeletedAt() != null) {
            return null;
        }
        
        String redisKey = generateRedisKey(clientRegistrationId, principalName);
        OAuth2AccessToken accessToken = (OAuth2AccessToken) cachePort.get(redisKey);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(oAuth2Client.getRefreshToken(), Instant.ofEpochMilli(oAuth2Client.getCreatedAt()));
        return (T) new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String principalName = principal.getName();
        String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
        
        OAuth2Client oAuth2Client = oAuth2ClientPort.findByProviderAndProviderId(registrationId, principalName);
        if (oAuth2Client == null) {
            OAuth2ClientCreate oAuth2ClientCreate = OAuth2ClientCreate.builder()
                .provider(registrationId)
                .providerId(principalName)
                .refreshToken(refreshToken.getTokenValue())
                .refreshTokenIssuedAt(refreshToken.getIssuedAt().toEpochMilli())
                .build();
            oAuth2ClientPort.create(oAuth2ClientCreate);
        } else if (refreshToken != null) {
            OAuth2ClientUpdate oAuth2ClientUpdata = OAuth2ClientUpdate.builder()
                .refreshToken(refreshToken.getTokenValue())
                .refreshTokenIssuedAt(refreshToken.getIssuedAt().toEpochMilli())
                .deletedAt(null)
                .build();
            oAuth2ClientPort.updateState(oAuth2Client.getId(), oAuth2ClientUpdata);
        }

        cachePort.save(generateRedisKey(registrationId, principalName), accessToken, accessToken.getExpiresAt().toEpochMilli());
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        OAuth2Client oAuth2Client = oAuth2ClientPort.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client == null) {
            return;
        }

        OAuth2ClientUpdate oAuth2ClientUpdate = OAuth2ClientUpdate.builder()
            .refreshToken(null)
            .refreshTokenIssuedAt(null)
            .deletedAt(System.currentTimeMillis())
            .build();
        oAuth2ClientPort.updateState(oAuth2Client.getId(), oAuth2ClientUpdate);
        cachePort.remove(generateRedisKey(clientRegistrationId, principalName));
    }

    private String generateRedisKey(String clientRegistrationId, String principalName) {
        return REDIS_KEY_PREFIX + clientRegistrationId + ":" + principalName;
    }
}
