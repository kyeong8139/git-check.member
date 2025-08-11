package com.git_check.member.auth.adapter.out.persistence;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;
import com.git_check.member.auth.application.port.out.OAuth2ClientPort;

@Component
public class OAuth2ClientRepositoryAdapter implements OAuth2ClientPort {
    private final OAuth2ClientJPARepository oAuth2ClientJPARepository;
    private final ClientAccessTokenHistoryJPARepository clientAccessTokenHistoryJPARepository;

    public OAuth2ClientRepositoryAdapter(OAuth2ClientJPARepository oAuth2ClientJPARepository, ClientAccessTokenHistoryJPARepository clientAccessTokenHistoryJPARepository) {
        this.oAuth2ClientJPARepository = oAuth2ClientJPARepository;
        this.clientAccessTokenHistoryJPARepository = clientAccessTokenHistoryJPARepository;
    }
    
    @Override
    public OAuth2Client findByProviderAndPrincipalName(String provider, String principalName) {
        return oAuth2ClientJPARepository.findByProviderAndPrincipalName(provider, principalName)
            .map(OAuth2ClientEntity::toModel)
            .orElse(null);
    }

    @Override
    public OAuth2Client create(OAuth2ClientCreate oAuth2ClientCreate) {
        OAuth2ClientEntity oAuth2ClientEntity = oAuth2ClientJPARepository.saveAndFlush(OAuth2ClientEntity.from(oAuth2ClientCreate));
        clientAccessTokenHistoryJPARepository.save(ClientAccessTokenEntity.from(oAuth2ClientCreate.getAccessToken(), oAuth2ClientEntity));
        return oAuth2ClientEntity.toModel();
    }

    @Override
    public void updateState(long id, OAuth2ClientUpdate oAuth2ClientUpdate) {
        oAuth2ClientJPARepository.updateState(id, oAuth2ClientUpdate);
        OAuth2ClientEntity oAuth2ClientEntity = oAuth2ClientJPARepository.findById(id);
        clientAccessTokenHistoryJPARepository.save(ClientAccessTokenEntity.from(oAuth2ClientUpdate.getAccessToken(), oAuth2ClientEntity));
    }

    @Override
    public OAuth2AccessToken findLastAccessTokenByProviderAndPrincipalName(long clientId) {
        return clientAccessTokenHistoryJPARepository.findLastAccessTokenByClientId(clientId)
            .map(ClientAccessTokenEntity::toModel)
            .orElse(null);
    }
}
