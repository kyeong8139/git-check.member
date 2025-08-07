package com.git_check.member.auth.mock;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;
import com.git_check.member.auth.application.port.out.OAuth2ClientPort;

import java.util.HashMap;
import java.util.Map;

public class FakeOAuth2ClientAdapter implements OAuth2ClientPort {

    private Map<Long, OAuth2Client> oAuth2ClientRepository = new HashMap<>();
    private long id = 0;
    private long currentTimeMillis;

    public FakeOAuth2ClientAdapter(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    @Override
    public OAuth2Client findByProviderAndProviderId(String provider, String providerId) {
        for (OAuth2Client oAuth2Client : oAuth2ClientRepository.values()) {
            if (oAuth2Client.getProvider().equals(provider) && oAuth2Client.getProviderId().equals(providerId)) {
                return oAuth2Client;
            }
        }
        return null;
    }

    @Override
    public void create(OAuth2ClientCreate oAuth2ClientCreate) {
        OAuth2Client oAuth2Client = OAuth2Client.builder()
            .provider(oAuth2ClientCreate.getProvider())
            .providerId(oAuth2ClientCreate.getProviderId())
            .refreshToken(oAuth2ClientCreate.getRefreshToken())
            .refreshTokenIssuedAt(oAuth2ClientCreate.getRefreshTokenIssuedAt())
            .build();
        oAuth2ClientRepository.put(id++, oAuth2Client);
    }

    @Override
    public void update(long id, OAuth2ClientUpdate oAuth2ClientUpdata) {
        OAuth2Client oAuth2Client = oAuth2ClientRepository.get(id);
        OAuth2Client updatedClient = OAuth2Client.builder()
            .id(oAuth2Client.getId())
            .provider(oAuth2Client.getProvider())
            .providerId(oAuth2Client.getProviderId())
            .refreshToken(oAuth2ClientUpdata.getRefreshToken())
            .refreshTokenIssuedAt(oAuth2ClientUpdata.getRefreshTokenIssuedAt())
            .deletedAt(oAuth2ClientUpdata.getDeletedAt())
            .build();
        oAuth2ClientRepository.put(id, updatedClient);
    }

    @Override
    public void delete(long id) {
        OAuth2ClientUpdate oAuth2ClientUpdate = OAuth2ClientUpdate.builder()
            .refreshToken(null)
            .refreshTokenIssuedAt(null)
            .deletedAt(currentTimeMillis)
            .build();
        this.update(id, oAuth2ClientUpdate);
    }
}
