package com.git_check.member.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import com.git_check.member.auth.repository.JPAOAuth2ClientRepository;
import com.git_check.member.auth.service.HybridOAuth2ClientRepositoryService;
import com.git_check.member.auth.service.RedisOAuth2TokenService;


@Configuration
public class OAuth2ClientConfig {

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository(JPAOAuth2ClientRepository jpaOAuth2ClientRepository, 
    RedisOAuth2TokenService redisOAuth2TokenService) {
        return new HybridOAuth2ClientRepositoryService(jpaOAuth2ClientRepository, redisOAuth2TokenService);
    }
}
