package com.social_media.apigateway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateLimitConfig {
    private final long limit;
    private final long windowSeconds;
}
