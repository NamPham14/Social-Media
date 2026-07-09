package com.social_media.identityservice.application.command;

import lombok.Builder;

@Builder
public record LogoutCommand(String accessToken, String refreshToken){
}
