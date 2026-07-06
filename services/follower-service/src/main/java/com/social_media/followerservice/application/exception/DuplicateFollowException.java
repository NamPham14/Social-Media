package com.social_media.followerservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class DuplicateFollowException extends BusinessRuleViolationException {
    public DuplicateFollowException() {
        super(FollowerError.DUPLICATE_FOLLOW.getCode(), FollowerError.DUPLICATE_FOLLOW.getMessage());
    }
}
