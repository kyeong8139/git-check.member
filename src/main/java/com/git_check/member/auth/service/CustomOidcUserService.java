package com.git_check.member.auth.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import com.git_check.member.global.dto.MemberInfo;
import com.git_check.member.global.dto.MemberRegisterDto;
import com.git_check.member.member.service.MemberAccountService;
import com.git_check.member.auth.OidcPrincipalDetail;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final MemberAccountService memberAccountService;

    public CustomOidcUserService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }   

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest){
        OidcUser oidcUser = super.loadUser(userRequest);
        String socialLoginType = userRequest.getClientRegistration().getRegistrationId();
        String socialLoginId = oidcUser.getIdToken().getSubject();
        MemberInfo memberInfo = memberAccountService.findMember(socialLoginType, socialLoginId);
        
        if(memberInfo == null){
            MemberRegisterDto newSocialMember = createMemberRegisterDto(userRequest);
            memberInfo = memberAccountService.registerAccount(newSocialMember);
        }

        return new OidcPrincipalDetail(memberInfo, oidcUser);
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
