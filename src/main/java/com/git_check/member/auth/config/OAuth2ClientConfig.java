package com.git_check.member.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.git_check.member.auth.repository.JPAOAuth2ClientRepository;
import com.git_check.member.auth.service.RedisOAuth2TokenService;
import com.git_check.member.auth.service.HybridOAuth2ClientRepositoryService;

@Configuration
public class OAuth2ClientConfig {

    private final JPAOAuth2ClientRepository jpaOAuth2ClientRepository;
    private final RedisOAuth2TokenService redisOAuth2TokenService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2ClientConfig(JPAOAuth2ClientRepository jpaOAuth2ClientRepository, RedisOAuth2TokenService redisOAuth2TokenService, ClientRegistrationRepository clientRegistrationRepository) {
        this.jpaOAuth2ClientRepository = jpaOAuth2ClientRepository;
        this.redisOAuth2TokenService = redisOAuth2TokenService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService() {
        return new HybridOAuth2ClientRepositoryService(jpaOAuth2ClientRepository, redisOAuth2TokenService, clientRegistrationRepository);
    }
}
