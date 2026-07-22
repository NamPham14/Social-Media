package com.social_media.chatservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class NotParticipantException extends BusinessRuleViolationException {
    public NotParticipantException() {
        super(ChatError.NOT_A_PARTICIPANT.getCode(), ChatError.NOT_A_PARTICIPANT.getMessage());
    }
}
