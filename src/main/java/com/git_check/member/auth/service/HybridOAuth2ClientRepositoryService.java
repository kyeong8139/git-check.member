package com.git_check.member.auth.service;

import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.git_check.member.auth.repository.JPAOAuth2ClientRepository;
import com.git_check.member.auth.repository.OAuth2Client;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.time.LocalDateTime;

public class HybridOAuth2ClientRepositoryService implements OAuth2AuthorizedClientRepository {

    private final JPAOAuth2ClientRepository jpaOAuth2ClientRepository;
    private final RedisOAuth2TokenService redisOAuth2TokenService;

    public HybridOAuth2ClientRepositoryService(JPAOAuth2ClientRepository jpaOAuth2ClientRepository, 
                                      RedisOAuth2TokenService redisOAuth2TokenService) {
        this.jpaOAuth2ClientRepository = jpaOAuth2ClientRepository;
        this.redisOAuth2TokenService = redisOAuth2TokenService;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
        String principalName = principal.getName();
        
        String accessToken = redisOAuth2TokenService.getAccessToken(clientRegistrationId, principalName);
        
        OAuth2Client jpaOAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(clientRegistrationId, principalName);

        // TODO: Access Token과 Refresh Token을 조합하여 OAuth2AuthorizedClient 객체 생성
        // 현재는 null 반환하지만, 실제로는 토큰들을 조합하여 OAuth2AuthorizedClient를 생성해야 함
        
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal,
            HttpServletRequest request, HttpServletResponse response) {
        String provider = authorizedClient.getClientRegistration().getRegistrationId();
        String principalName = principal.getName();
        
        if (authorizedClient.getAccessToken() != null) {
            redisOAuth2TokenService.saveAccessToken(provider, principalName, 
                authorizedClient.getAccessToken().getTokenValue());
        }
        
        OAuth2Client oAuth2Client = OAuth2Client.builder()
                .provider(provider)
                .providerId(principalName)
                .refreshToken(authorizedClient.getRefreshToken().getTokenValue())
                .scope(authorizedClient.getClientRegistration().getScopes().toString())
                .build();

        jpaOAuth2ClientRepository.save(oAuth2Client);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, Authentication principal,
        HttpServletRequest request, HttpServletResponse response) {
        String principalName = principal.getName();
        
        redisOAuth2TokenService.removeAccessToken(clientRegistrationId, principalName);
        
        OAuth2Client oAuth2Client = jpaOAuth2ClientRepository.findByProviderAndProviderId(clientRegistrationId, principalName);
        if (oAuth2Client != null) {
            jpaOAuth2ClientRepository.updateDeletedAt(oAuth2Client, LocalDateTime.now());
        }
    }
}
