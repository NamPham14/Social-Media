package com.social_media.chatservice.domain.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class UnauthorizedChatActionException extends BusinessRuleViolationException {
    public UnauthorizedChatActionException() {
        this("Unauthorized chat action");
    }

    public UnauthorizedChatActionException(String message) {
        super(401, message);
    }
}
