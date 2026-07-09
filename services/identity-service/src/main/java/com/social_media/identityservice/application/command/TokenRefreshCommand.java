package com.social_media.identityservice.application.command;

import lombok.Builder;

@Builder
public record TokenRefreshCommand (String accessToken, String refreshToken) {}
