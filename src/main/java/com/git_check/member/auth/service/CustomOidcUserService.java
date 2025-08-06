package com.git_check.member.auth.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;
import com.git_check.member.member.service.MemberAccountService;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser>{

    private final MemberAccountService memberAccountService;

    public CustomOidcUserService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest){
        String socialLoginType = userRequest.getClientRegistration().getRegistrationId();
        String socialLoginId = userRequest.getIdToken().getSubject();
        MemberInfo memberInfo = memberAccountService.findMember(socialLoginType, socialLoginId);
        
        if(memberInfo == null){
            MemberRegisterDto newSocialMember = createMemberRegisterDto(userRequest);
            memberInfo = memberAccountService.registerAccount(newSocialMember);
        }

        return createOidcUser(memberInfo);
    }
    
    private OidcUser createOidcUser(MemberInfo memberInfo){
        return null;
    }

    private MemberRegisterDto createMemberRegisterDto(OidcUserRequest userRequest){
        return null;
    }
}
