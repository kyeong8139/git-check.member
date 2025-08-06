package com.git_check.member.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService;
    
    public SecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService, 
                         OAuth2AuthorizationRequestResolver authorizationRequestResolver,
                         OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.authorizationRequestResolver = authorizationRequestResolver;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/login").permitAll()
            .anyRequest().authenticated());
        
        http.oauth2Login(oauth2 -> oauth2
            .authorizedClientService(this.oAuth2AuthorizedClientService)
            .authorizationEndpoint(authorization -> authorization
                .authorizationRequestResolver(authorizationRequestResolver))
            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
        );

        return http.build();
    }
}
