package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.api.dto.response.NotificationResponse;
import com.social_media.notificationservice.application.mapper.NotificationApiMapper;
import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetNotificationsUseCase {
    private final NotificationRepository notificationRepository;
    private final NotificationApiMapper mapper;

    public List<NotificationResponse> execute(String currentUserId, int limit) {
        return notificationRepository.findByRecipientId(currentUserId, limit)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}

