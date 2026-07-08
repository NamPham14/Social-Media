package com.social_media.notificationservice.application.usecase;

import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.social_media.notificationservice.application.command.CreateNotificationFromEventCommand;

@Service
@RequiredArgsConstructor
public class CreateNotificationFromEventUseCase {
    private final NotificationRepository notificationRepository;

    public Notification handle(CreateNotificationFromEventCommand command){
        if(notificationRepository.existsBySourceEventId(command.sourceEventId())){
            return notificationRepository.findBySourceEventId(command.sourceEventId()).orElseThrow();
        }
        Notification notification = Notification.createFromEvent(
                command.sourceEventId(),
                command.recipientId(),
                command.actorId(),
                command.notificationType(),
                command.targetType(),
                command.targetId(),
                command.message()
        );
        return notificationRepository.save(notification);
    }
}
