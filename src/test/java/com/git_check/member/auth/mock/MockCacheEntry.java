package com.git_check.member.auth.mock;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockCacheEntry {
    private Object value;
	private long expiresAt;
}
