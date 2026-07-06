package com.social_media.followerservice.application.exception;
import com.social_media.common.exception.AppException;

public class DuplicateFollowException extends AppException {
    public DuplicateFollowException() { super(FollowerError.DUPLICATE_FOLLOW); }
}