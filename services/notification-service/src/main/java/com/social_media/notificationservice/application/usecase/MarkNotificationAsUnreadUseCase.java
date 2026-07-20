package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkNotificationAsUnreadUseCase {
    private final NotificationRepository notificationRepository;

    public void execute(Long notificationId, String currentUserId) {
        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.markAsUnread(currentUserId);
        notificationRepository.save(notification);
    }
}

