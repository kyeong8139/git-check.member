package com.git_check.member.auth.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;    
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    
    public SecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService, 
                         OAuth2AuthorizationRequestResolver authorizationRequestResolver,
                         OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService,
                         AuthenticationSuccessHandler authenticationSuccessHandler,
                         AuthenticationFailureHandler authenticationFailureHandler) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.authorizationRequestResolver = authorizationRequestResolver;
        this.oidcUserService = oidcUserService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
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
            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
        );

        return http.build();
    }
}
