package com.git_check.member.auth.mock;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;
import com.git_check.member.auth.application.domain.dto.OAuth2ClientUpdate;
import com.git_check.member.auth.application.port.out.OAuth2ClientPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

public class FakeOAuth2ClientAdapter implements OAuth2ClientPort {

    private Map<Long, OAuth2Client> oAuth2ClientRepository = new HashMap<>();
    private Map<Long, List<OAuth2AccessToken>> oAuth2ClientAccessTokenRepository = new HashMap<>();
    private long id = 0;
    private long currentTimeMills;
    private long futureTimeMills;
    private int getTokenCount = 0;

    public FakeOAuth2ClientAdapter(long currentTimeMills) {
        this.currentTimeMills = currentTimeMills;
        this.futureTimeMills = currentTimeMills * 2;
    }

    @Override
    public OAuth2Client findByProviderAndPrincipalName(String provider, String principalName) {
        for (OAuth2Client oAuth2Client : oAuth2ClientRepository.values()) {
            if (oAuth2Client.getProvider().equals(provider) && oAuth2Client.getPrincipalName().equals(principalName)) {
                return oAuth2Client;
            }
        }
        return null;
    }

    @Override
    public OAuth2Client create(OAuth2ClientCreate oAuth2ClientCreate) {
        OAuth2Client oAuth2Client = OAuth2Client.builder()
            .id(id)
            .provider(oAuth2ClientCreate.getProvider())
            .principalName(oAuth2ClientCreate.getPrincipalName())
            .accessToken(oAuth2ClientCreate.getAccessToken())
            .refreshToken(oAuth2ClientCreate.getRefreshToken())
            .build();
        oAuth2ClientRepository.put(id, oAuth2Client);
        oAuth2ClientAccessTokenRepository.put(id, new ArrayList<>());
        oAuth2ClientAccessTokenRepository.get(id).add(oAuth2ClientCreate.getAccessToken());
        return oAuth2ClientRepository.get(id++);
    }

    @Override
    public OAuth2Client updateState(long id, OAuth2ClientUpdate oAuth2ClientUpdata) {
        OAuth2Client oAuth2Client = oAuth2ClientRepository.get(id);
        OAuth2Client updatedClient = OAuth2Client.builder()
            .id(oAuth2Client.getId())
            .provider(oAuth2Client.getProvider())
            .principalName(oAuth2Client.getPrincipalName())
            .refreshToken(oAuth2ClientUpdata.getRefreshToken())
            .deletedAt(oAuth2ClientUpdata.getDeletedAt())
            .build();
        oAuth2ClientRepository.put(id, updatedClient);
        return oAuth2ClientRepository.get(id);
    }

    @Override
    public OAuth2AccessToken findLastAccessTokenByProviderAndPrincipalName(long clientId) {
        getTokenCount++;
        return oAuth2ClientAccessTokenRepository.get(clientId).get(oAuth2ClientAccessTokenRepository.get(clientId).size() - 1);
    }
    
    public OAuth2Client createSoftDeletedClient(String provider, String principalName) {
        OAuth2Client oAuth2Client = OAuth2Client.builder()
            .id(id)
            .provider(provider)
            .principalName(principalName)
            .accessToken(null)
            .refreshToken(null)
            .deletedAt(currentTimeMills)
            .build();
        oAuth2ClientRepository.put(id, oAuth2Client);
        return oAuth2ClientRepository.get(id++);
    }

    public OAuth2Client createActiveUser(String clientRegistrationId, String principalName) {
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER, 
            "DUMMY_TOKEN", 
            Instant.ofEpochMilli(currentTimeMills),  
            Instant.ofEpochMilli(futureTimeMills));

        OAuth2RefreshToken oAuth2RefreshToken = new OAuth2RefreshToken(
            "DUMMY_REFRESH_TOKEN",
            Instant.ofEpochMilli(currentTimeMills),
            Instant.ofEpochMilli(futureTimeMills));

        OAuth2Client oAuth2Client = OAuth2Client.builder()
            .id(id)
            .provider(clientRegistrationId)
            .principalName(principalName)
            .accessToken(oAuth2AccessToken)
            .refreshToken(oAuth2RefreshToken)
            .deletedAt(null)
            .build();
        oAuth2ClientRepository.put(id, oAuth2Client);
        oAuth2ClientAccessTokenRepository.put(id, new ArrayList<>());
        oAuth2ClientAccessTokenRepository.get(id).add(oAuth2AccessToken);
        return oAuth2ClientRepository.get(id++);
    }

    public int getTokenCount() {
        return getTokenCount;
    }
}
