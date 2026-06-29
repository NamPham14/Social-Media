package com.social_media.profileservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class DuplicateProfileException extends BusinessRuleViolationException {
    public DuplicateProfileException() {
        super(ProfileError.PROFILE_ALREADY_EXISTS.getCode(), ProfileError.PROFILE_ALREADY_EXISTS.getMessage());
    }
}
