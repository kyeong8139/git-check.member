package com.git_check.member.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public SecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.oauth2Login(oauth2 -> oauth2.authorizedClientService(oAuth2AuthorizedClientService));

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}
