package com.social_media.followerservice.application.exception;
import com.social_media.common.exception.AppException;

public class CannotFollowSelfException extends AppException {
    public CannotFollowSelfException() { super(FollowerError.CANNOT_FOLLOW_SELF); }
}