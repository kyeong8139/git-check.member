package com.git_check.member.auth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.git_check.member.global.dto.MemberInfo;

public class OidcPrincipalDetail implements OidcUser{
    private final MemberInfo memberInfo;
    private final OidcUser oidcUser;
    public OidcPrincipalDetail(MemberInfo memberInfo, OidcUser oidcUser) {
        this.memberInfo = memberInfo;
        this.oidcUser = oidcUser;
    }

    public long getMemberId(){
        return memberInfo.getId();
    }

    public String getMemberName(){
        return memberInfo.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public String getName() {
        return oidcUser.getName();
    }
    
    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }
}
