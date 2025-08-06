package com.git_check.member.auth.service;

import com.git_check.member.auth.OidcPrincipalDetail;

public interface JwtService {
    public String createAccessToken(OidcPrincipalDetail OidcPrincipal);
    public String createRefreshToken(OidcPrincipalDetail OidcPrincipal);
    public void expireAllToken(OidcPrincipalDetail OidcPrincipal);
}
