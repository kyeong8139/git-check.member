package com.git_check.member.auth.application.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;
import com.git_check.member.auth.application.domain.OidcPrincipal;
import com.git_check.member.auth.application.port.out.MemberAccountPort;

@Service
public class OidcPrincipalService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final MemberAccountPort memberAccountService;
    private final OidcUserService oidcUserService;
    
    public OidcPrincipalService(MemberAccountPort memberAccountService) {
        this.memberAccountService = memberAccountService;
        this.oidcUserService = new OidcUserService();
    }   

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest){
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);
        String socialLoginType = userRequest.getClientRegistration().getRegistrationId();
        String socialLoginId = oidcUser.getIdToken().getSubject();
        MemberInfo memberInfo = memberAccountService.getMemberInfo(socialLoginType, socialLoginId);
        
        if(memberInfo == null){
            MemberRegisterDto newSocialMember = createMemberRegisterDto(userRequest);
            memberInfo = memberAccountService.registerMember(newSocialMember);
        }

        return new OidcPrincipal(memberInfo, oidcUser);
    }

    private MemberRegisterDto createMemberRegisterDto(OidcUserRequest userRequest){
        String socialLoginType = userRequest.getClientRegistration().getRegistrationId();
        String socialLoginId = userRequest.getIdToken().getSubject();
        
        return MemberRegisterDto.builder()
                .socialLoginType(socialLoginType)
                .socialLoginId(socialLoginId)
                .build();
    }
}
