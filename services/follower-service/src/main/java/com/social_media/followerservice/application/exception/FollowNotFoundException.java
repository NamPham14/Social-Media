package com.social_media.followerservice.application.exception;

import com.social_media.common.exception.EntityNotFoundException;

public class FollowNotFoundException extends EntityNotFoundException {
    public FollowNotFoundException() {
        super(FollowerError.FOLLOW_NOT_FOUND.getMessage());
    }
}
