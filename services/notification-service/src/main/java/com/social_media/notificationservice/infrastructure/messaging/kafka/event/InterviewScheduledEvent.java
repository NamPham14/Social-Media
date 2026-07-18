package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record InterviewScheduledEvent(
        String eventId,
        String interviewId,
        String candidateUserId,
        String recruiterUserId,
        String recruiterName,
        String scheduledAt
) {
}
