package com.git_check.member.auth.adapter.config.handler;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.git_check.member.auth.application.domain.OidcPrincipal;
import com.git_check.member.auth.application.port.in.ProvideJwtToken;

@Component
public class Odic2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ProvideJwtToken jwtService;

    public Odic2LoginSuccessHandler(ProvideJwtToken jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OidcPrincipal oidcPrincipal = (OidcPrincipal) authentication.getPrincipal();
        String refreshToken = jwtService.createRefreshToken(oidcPrincipal);
                
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Set-Cookie", "refreshToken=" + refreshToken + "; Path=/; HttpOnly; SameSite=Strict");
    }
}
