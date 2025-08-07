package com.git_check.member.auth.application.port.in;

import com.git_check.member.auth.application.domain.OidcPrincipal;

public interface ProvideJwtToken {
    public String createAccessToken(OidcPrincipal OidcPrincipal);
    public String createRefreshToken(OidcPrincipal OidcPrincipal);
    public void expireAllToken(OidcPrincipal OidcPrincipal);
}
