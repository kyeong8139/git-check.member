package com.git_check.member.auth.application.port.in;

import com.git_check.member.auth.application.domain.OidcPrincipal;
import com.git_check.member.auth.application.domain.dto.JwtToken;

public interface JwtTokenPort {
    public String createAccessToken(OidcPrincipal OidcPrincipal);
    public String createRefreshToken(OidcPrincipal OidcPrincipal);

    public JwtToken reissueToken(String refreshToken);
}
