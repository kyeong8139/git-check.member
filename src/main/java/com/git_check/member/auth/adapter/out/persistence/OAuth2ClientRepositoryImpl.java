package com.git_check.member.auth.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;
import com.git_check.member.auth.application.port.out.OAuth2ClientPort;

@Repository
public class OAuth2ClientRepositoryImpl implements OAuth2ClientPort {
    private final OAuth2ClientJPARepository oAuth2ClientJPARepository;

    public OAuth2ClientRepositoryImpl(OAuth2ClientJPARepository oAuth2ClientJPARepository) {
        this.oAuth2ClientJPARepository = oAuth2ClientJPARepository;
    }
    
    @Override
    public OAuth2Client findByProviderAndProviderId(String provider, String providerId) {
        return oAuth2ClientJPARepository.findByProviderAndProviderId(provider, providerId)
            .map(OAuth2ClientEntity::toModel)
            .orElse(null);
    }

    @Override
    public void create(OAuth2ClientCreate oAuth2ClientCreate) {
        oAuth2ClientJPARepository.save(OAuth2ClientEntity.from(oAuth2ClientCreate));
    }

    @Override
    public void updateState(long id, OAuth2ClientUpdate oAuth2ClientUpdate) {
        oAuth2ClientJPARepository.updateState(id, oAuth2ClientUpdate);
    }
}
