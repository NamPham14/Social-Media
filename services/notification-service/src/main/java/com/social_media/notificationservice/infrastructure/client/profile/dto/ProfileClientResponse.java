package com.social_media.notificationservice.infrastructure.client.profile.dto;

import java.util.UUID;

public record ProfileClientResponse(
        UUID id,
        String username,
        String fullName,
        String bio,
        String avatarUrl
) {
}
