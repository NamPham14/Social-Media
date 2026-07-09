package com.social_media.profileservice.application.exception;

import com.social_media.common.exception.EntityNotFoundException;

public class ProfileNotFoundException extends EntityNotFoundException {
    public ProfileNotFoundException() {
        super(ProfileError.PROFILE_NOT_FOUND.getMessage());
    }
}
