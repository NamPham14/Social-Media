package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteNotificationsByTargetUseCase {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void handle(TargetType targetType, String targetId) {
        notificationRepository.deleteByTargetTypeAndTargetId(targetType, targetId);
    }
}
