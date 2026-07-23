package com.social_media.notificationservice.application.mapper;

import com.social_media.common.api.ApiResponse;
import com.social_media.notificationservice.api.dto.response.NotificationResponse;
import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.infrastructure.client.profile.ProfileClient;
import com.social_media.notificationservice.infrastructure.client.profile.dto.ProfileClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationApiMapper {

    private final ProfileClient profileClient;

    public NotificationResponse toResponse(Notification notification) {
        ProfileDisplay actor = getActorProfile(notification.getActorId());

        return new NotificationResponse(
                notification.getId(),
                notification.getActorId(),
                actor.name(),
                actor.avatarUrl(),
                notification.getNotificationType().name(),
                notification.getTargetType().name(),
                notification.getTargetId(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    private ProfileDisplay getActorProfile(String actorId) {
        try {
            ApiResponse<ProfileClientResponse> response = profileClient.getProfile(UUID.fromString(actorId));
            ProfileClientResponse profile = response.getData();
            if (profile == null) {
                return ProfileDisplay.fallback(actorId);
            }

            String displayName = profile.fullName() != null && !profile.fullName().isBlank()
                    ? profile.fullName()
                    : profile.username();

            return new ProfileDisplay(displayName, profile.avatarUrl());
        } catch (Exception e) {
            log.error("Failed to fetch profile for actorId: " + actorId, e);
            return ProfileDisplay.fallback(actorId);
        }
    }

    private record ProfileDisplay(String name, String avatarUrl) {
        static ProfileDisplay fallback(String actorId) {
            return new ProfileDisplay(actorId, null);
        }
    }
}
