package com.social_media.chatservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class CannotCreateConversationWithSelfException extends BusinessRuleViolationException {
    public CannotCreateConversationWithSelfException() {
        super(ChatError.CANNOT_CREATE_WITH_SELF.getCode(), ChatError.CANNOT_CREATE_WITH_SELF.getMessage());
    }
}
