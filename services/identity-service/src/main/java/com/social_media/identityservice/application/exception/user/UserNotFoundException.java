package com.social_media.identityservice.application.exception.user;

import com.social_media.common.exception.EntityNotFoundException;
import com.social_media.identityservice.application.exception.IdentityError;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException() {
        super(IdentityError.USER_NOT_FOUND.getMessage());
    }
}
