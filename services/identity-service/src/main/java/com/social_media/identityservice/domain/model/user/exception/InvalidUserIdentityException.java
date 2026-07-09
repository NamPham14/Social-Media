package com.social_media.identityservice.domain.model.user.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class InvalidUserIdentityException extends BusinessRuleViolationException {
    public InvalidUserIdentityException(String message) {
        super(1003, message); 
    }
}
