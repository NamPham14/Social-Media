package com.social_media.followerservice.application.exception;

import com.social_media.common.exception.BusinessRuleViolationException;

public class CannotFollowSelfException extends BusinessRuleViolationException {
    public CannotFollowSelfException() {
        super(FollowerError.CANNOT_FOLLOW_SELF.getCode(), FollowerError.CANNOT_FOLLOW_SELF.getMessage());
    }
}
