package com.git_check.member.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService, 
                         ClientRegistrationRepository clientRegistrationRepository) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> {
            oauth2.authorizedClientService(oAuth2AuthorizedClientService);
            
            /**
             * Todo : 개발 완료 시 제거
             */
            oauth2.authorizationEndpoint(authorization -> 
                authorization.authorizationRequestResolver(
                    new AlwaysGetRefreshTokenAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")
                )
            );
        });

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}
