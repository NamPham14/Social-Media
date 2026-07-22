package com.social_media.chatservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class NotSenderException extends BusinessRuleViolationException {
    public NotSenderException() {
        super(ChatError.NOT_SENDER.getCode(), ChatError.NOT_SENDER.getMessage());
    }
}
