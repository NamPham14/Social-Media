package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountUnreadNotificationsUseCase {
    private final NotificationRepository notificationRepository;

    public Long execute(Long currentUserId) {
        return notificationRepository.countUnreadByRecipientId(currentUserId);
    }
}
