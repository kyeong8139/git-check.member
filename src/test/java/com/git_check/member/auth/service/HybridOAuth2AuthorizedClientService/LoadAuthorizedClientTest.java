package com.git_check.member.auth.service.HybridOAuth2AuthorizedClientService;

import org.junit.jupiter.api.BeforeEach;

import com.git_check.member.auth.application.domain.OAuth2Client;
import com.git_check.member.auth.application.service.HybridOAuth2AuthorizedClientService;
import com.git_check.member.auth.mock.FakeCacheAdapter;
import com.git_check.member.auth.mock.FakeClientRegistrationAdapter;
import com.git_check.member.auth.mock.FakeOAuth2ClientAdapter;
import com.git_check.member.auth.adapter.config.exception.InvalidOAuth2ProviderException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class LoadAuthorizedClientTest {
    
    private HybridOAuth2AuthorizedClientService hybridOAuth2AuthorizedClientService;
    private FakeClientRegistrationAdapter fakeClientRegistrationAdapter;
    private FakeOAuth2ClientAdapter fakeOAuth2ClientAdapter;
    private FakeCacheAdapter fakeCacheAdapter;

    private long currentTimeMills = 100_0000_0000L;
    private String REDIS_KEY_PREFIX = "oauth2:access_token:";
    private String clientRegistrationId = "google";

    @BeforeEach
    void init() {
        this.fakeCacheAdapter = new FakeCacheAdapter(currentTimeMills);
        this.fakeClientRegistrationAdapter = new FakeClientRegistrationAdapter(clientRegistrationId);
        this.fakeOAuth2ClientAdapter = new FakeOAuth2ClientAdapter(currentTimeMills);
        this.hybridOAuth2AuthorizedClientService = HybridOAuth2AuthorizedClientService.builder()
            .clientRegistrationRepository(fakeClientRegistrationAdapter)
            .cachePort(fakeCacheAdapter)
            .oAuth2ClientPort(fakeOAuth2ClientAdapter)
            .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalidClientRegistrationId"})
    @NullSource
    void loadAuthorizedClient_withInvalidClientRegistrationId_throwsInvalidOAuth2ProviderException(String invalidClientRegistrationId) {
        // given
        String principalName = "1234567890";

        // when & then
        assertThrows(InvalidOAuth2ProviderException.class, () -> {
            hybridOAuth2AuthorizedClientService.loadAuthorizedClient(invalidClientRegistrationId, principalName);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"nonExistentPrincipalName"})
    @NullSource
    void loadAuthorizedClient_whenOAuth2ClientNotFound_returnsNull(String invalidPrincipalName) {
        // given
        String existentClientRegistrationId = clientRegistrationId;
        
        // when
        OAuth2AuthorizedClient authorizedClient = hybridOAuth2AuthorizedClientService.loadAuthorizedClient(existentClientRegistrationId, invalidPrincipalName);
        
        // then
        assertNull(authorizedClient);
    }

    @Test
    void loadAuthorizedClient_whenOAuth2ClientSoftDeleted_returnsNull() {
        // given
        String principalName = "1234567890";
        fakeOAuth2ClientAdapter.createSoftDeletedClient(clientRegistrationId, principalName);

        // when
        OAuth2AuthorizedClient authorizedClient = hybridOAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName);

        // then
        assertNull(authorizedClient);
    }

    @Test
    void loadAuthorizedClient_whenAccessTokenCached_returnsAuthorizedClientWithCachedAccessTokens(){
        //given
        String principalName = "1234567890";
        OAuth2Client client = fakeOAuth2ClientAdapter.createActiveUser(clientRegistrationId, principalName);
        String cacheKey = REDIS_KEY_PREFIX + clientRegistrationId + ":" + principalName;
        fakeCacheAdapter.save(cacheKey, client.getAccessToken());

        // when
        OAuth2AuthorizedClient authorizedClient = hybridOAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName);

        // then
        assertEquals(0, fakeOAuth2ClientAdapter.getTokenCount());
        assertEquals(1, fakeCacheAdapter.getTokenCount());
        assertEquals(client.getAccessToken().getTokenType(), authorizedClient.getAccessToken().getTokenType());
        assertEquals(client.getAccessToken().getTokenValue(), authorizedClient.getAccessToken().getTokenValue());
        assertEquals(client.getAccessToken().getIssuedAt(), authorizedClient.getAccessToken().getIssuedAt());
        assertEquals(client.getAccessToken().getExpiresAt(), authorizedClient.getAccessToken().getExpiresAt());
    }

    @Test
    void loadAuthorizedClient_whenAccessTokenMissing_returnsAuthorizedClientWithTokens(){
        String principalName = "1234567890";
        OAuth2Client client = fakeOAuth2ClientAdapter.createActiveUser(clientRegistrationId, principalName);

        // when
        OAuth2AuthorizedClient authorizedClient = hybridOAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName);

        // then
        assertEquals(0, fakeCacheAdapter.getTokenCount());
        assertEquals(1, fakeOAuth2ClientAdapter.getTokenCount());
        assertEquals(client.getAccessToken().getTokenType(), authorizedClient.getAccessToken().getTokenType());
        assertEquals(client.getAccessToken().getTokenValue(), authorizedClient.getAccessToken().getTokenValue());
        assertEquals(client.getAccessToken().getIssuedAt(), authorizedClient.getAccessToken().getIssuedAt());
        assertEquals(client.getAccessToken().getExpiresAt(), authorizedClient.getAccessToken().getExpiresAt());
    
    }
}
