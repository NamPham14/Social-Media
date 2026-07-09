package com.social_media.identityservice.application.exception.user;

import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.identityservice.application.exception.IdentityError;

public class UnauthenticatedException extends BusinessRuleViolationException {
    public UnauthenticatedException() {
        super(IdentityError.UNAUTHENTICATED.getCode(), IdentityError.UNAUTHENTICATED.getMessage());
    }
}
