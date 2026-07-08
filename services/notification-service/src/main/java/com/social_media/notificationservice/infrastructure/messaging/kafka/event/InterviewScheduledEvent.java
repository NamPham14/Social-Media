package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record InterviewScheduledEvent(
        String eventId,
        Long interviewId,
        Long candidateUserId,
        Long recruiterUserId,
        String recruiterName,
        String scheduledAt
) {
}