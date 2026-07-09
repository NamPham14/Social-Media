package com.social_media.postservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class DuplicateResourceException extends BusinessRuleViolationException {
    public DuplicateResourceException() {
        super(PostError.DUPLICATE_RESOURCE.getCode(), PostError.DUPLICATE_RESOURCE.getMessage());
    }
}
