package com.git_check.member.auth.service;

import org.junit.jupiter.api.BeforeEach;

import com.git_check.member.auth.application.service.HybridOAuth2AuthorizedClientService;
import com.git_check.member.auth.mock.FakeCacheAdapter;
import com.git_check.member.auth.mock.FakeClientRegistrationAdapter;
import com.git_check.member.auth.mock.FakeOAuth2ClientAdapter;

import com.git_check.member.auth.application.domain.dto.OAuth2ClientCreate;

public class HybridOAuth2AuthorizedClientServiceTest {
    
    private HybridOAuth2AuthorizedClientService hybridOAuth2AuthorizedClientService;
    private long postTimeMills = 10_0000_0000L;
    private long currentTimeMills = 100_0000_0000L;
    private long futureTimeMills = 1000_0000_0000L;
    private String REDIS_KEY_PREFIX = "oauth2:access_token:";

    @BeforeEach
    void init() {
        FakeCacheAdapter fakeCacheAdapter = new FakeCacheAdapter(currentTimeMills);
        FakeClientRegistrationAdapter fakeClientRegistrationAdapter = new FakeClientRegistrationAdapter("google");
        FakeOAuth2ClientAdapter fakeOAuth2ClientAdapter = new FakeOAuth2ClientAdapter();
        this.hybridOAuth2AuthorizedClientService = HybridOAuth2AuthorizedClientService.builder()
            .clientRegistrationRepository(fakeClientRegistrationAdapter)
            .cachePort(fakeCacheAdapter)
            .oAuth2ClientPort(fakeOAuth2ClientAdapter)
            .build();

        OAuth2ClientCreate unExpiredAccessTokenClient = OAuth2ClientCreate.builder()
            .provider("google")
            .providerId("1234567890")
            .refreshToken("refreshToken1")
            .refreshTokenIssuedAt(postTimeMills)
            .build();
        
        OAuth2ClientCreate expiredAccessTokenClient = OAuth2ClientCreate.builder()
            .provider("google")
            .providerId("1234567891")
            .refreshToken("refreshToken2")
            .refreshTokenIssuedAt(postTimeMills)
            .build();

        fakeOAuth2ClientAdapter.create(unExpiredAccessTokenClient);
        fakeOAuth2ClientAdapter.create(expiredAccessTokenClient);

        String key1 = REDIS_KEY_PREFIX + unExpiredAccessTokenClient.getProvider() + ":" + unExpiredAccessTokenClient.getProviderId();
        String key2 = REDIS_KEY_PREFIX + expiredAccessTokenClient.getProvider() + ":" + expiredAccessTokenClient.getProviderId();
        fakeCacheAdapter.save(key1, "accessToken1", futureTimeMills);
        fakeCacheAdapter.save(key2, "accessToken2", postTimeMills);
    }
}
