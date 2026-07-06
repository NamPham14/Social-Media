package com.social_media.followerservice.application.exception;
import com.social_media.common.exception.AppException;

public class FollowNotFoundException extends AppException {
    public FollowNotFoundException() { super(FollowerError.FOLLOW_NOT_FOUND); }
}