package com.git_check.member.auth.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.git_check.member.auth.application.domain.dto.JwtToken;
import com.git_check.member.auth.application.port.in.JwtTokenPort;

@RestController("/auth")
public class AuthController {

    private final JwtTokenPort jwtTokenPort;

    public AuthController(JwtTokenPort jwtTokenPort) {
        this.jwtTokenPort = jwtTokenPort;
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@RequestHeader("refreshToken") String refreshToken) {
        JwtToken jwtToken = jwtTokenPort.reissueToken(refreshToken);
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(jwtToken.getAccessToken());
    }
}
