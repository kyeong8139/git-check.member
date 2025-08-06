package com.git_check.member.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.git_check.member.auth.repository.JPAOAuth2ClientRepository;
import com.git_check.member.auth.service.RedisOAuth2TokenService;
import com.git_check.member.auth.service.HybridOAuth2ClientRepositoryService;


@Configuration
public class AuthorizedClientServiceConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RedisOAuth2TokenService redisOAuth2TokenService;
    private final JPAOAuth2ClientRepository jpaOAuth2ClientRepository;

    public AuthorizedClientServiceConfig(ClientRegistrationRepository clientRegistrationRepository, RedisOAuth2TokenService redisOAuth2TokenService, JPAOAuth2ClientRepository jpaOAuth2ClientRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.redisOAuth2TokenService = redisOAuth2TokenService;
        this.jpaOAuth2ClientRepository = jpaOAuth2ClientRepository;
    }
    
    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService() {
        return new HybridOAuth2ClientRepositoryService(this.clientRegistrationRepository, this.redisOAuth2TokenService, this.jpaOAuth2ClientRepository);
    }
}
