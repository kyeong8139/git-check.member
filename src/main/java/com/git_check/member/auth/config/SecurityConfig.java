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
    
    public SecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService, 
                         OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.authorizationRequestResolver = authorizationRequestResolver;
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
            // Todo : CustomOAuth2UserService에 회원가입/로그인 비즈니스 로직 구현 후 활성화
            // .userInfoEndpoint(userInfo -> userInfo.userService(new CustomOAuth2UserService()))
        );

        return http.build();
    }


}
