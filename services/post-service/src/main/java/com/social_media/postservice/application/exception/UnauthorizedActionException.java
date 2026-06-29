package com.social_media.postservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class UnauthorizedActionException extends BusinessRuleViolationException {
    public UnauthorizedActionException() {
        super(PostError.UNAUTHORIZED_ACTION.getCode(), PostError.UNAUTHORIZED_ACTION.getMessage());
    }
}
