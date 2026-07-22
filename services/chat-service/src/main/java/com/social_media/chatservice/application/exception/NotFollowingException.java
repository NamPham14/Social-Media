package com.social_media.chatservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

import java.util.UUID;

public class NotFollowingException extends BusinessRuleViolationException {
    public NotFollowingException(UUID otherUserId) {
        super(ChatError.NOT_FOLLOWING.getCode(), "You must follow " + otherUserId + " to start a conversation.");
    }
}
