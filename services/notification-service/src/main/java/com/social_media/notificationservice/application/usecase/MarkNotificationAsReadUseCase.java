package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkNotificationAsReadUseCase {
    private final NotificationRepository notificationRepository;

    public void execute(Long notificationId, Long currentUserId) {
        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.markAsRead(currentUserId);
        notificationRepository.save(notification);
    }
}
