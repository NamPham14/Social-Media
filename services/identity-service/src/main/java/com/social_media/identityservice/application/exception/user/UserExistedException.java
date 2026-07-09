package com.social_media.identityservice.application.exception.user;

import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.identityservice.application.exception.IdentityError;

public class UserExistedException extends BusinessRuleViolationException {
    public UserExistedException() {
        super(IdentityError.USER_EXISTED.getCode(), IdentityError.USER_EXISTED.getMessage());
    }
}
