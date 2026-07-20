package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkAllNotificationsAsReadUseCase {
    private final NotificationRepository notificationRepository;

    public void execute(String currentUserId) {
        notificationRepository.findUnreadByRecipientId(currentUserId, 100)
                .forEach(notification -> {
                    notification.markAsRead(currentUserId);
                    notificationRepository.save(notification);
                });
    }
}

