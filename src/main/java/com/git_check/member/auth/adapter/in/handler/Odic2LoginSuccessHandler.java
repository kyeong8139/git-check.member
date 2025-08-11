package com.git_check.member.auth.adapter.in.handler;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.git_check.member.auth.application.domain.OidcPrincipal;
import com.git_check.member.auth.application.port.in.JwtTokenPort;

@Component
public class Odic2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenPort jwtService;

    public Odic2LoginSuccessHandler(JwtTokenPort jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OidcPrincipal oidcPrincipal = (OidcPrincipal) authentication.getPrincipal();
        String refreshToken = jwtService.createRefreshToken(oidcPrincipal);
        String accessToken = jwtService.createAccessToken(oidcPrincipal);
                
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        // .secure(true)    
        .path("/")
        .maxAge(Duration.ofDays(7))
        .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true)
        // .secure(true)       
        .path("/")
        .maxAge(Duration.ofMinutes(10))
        .build();

        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }
}
