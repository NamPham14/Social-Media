package com.social_media.followerservice.domain.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class UnauthorizedActionException extends BusinessRuleViolationException {
    public UnauthorizedActionException() {
        this("Unauthorized action");
    }

    public UnauthorizedActionException(String message) {
        super(401, message);
    }
}
