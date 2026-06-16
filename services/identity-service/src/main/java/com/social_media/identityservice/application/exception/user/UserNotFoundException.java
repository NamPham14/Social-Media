package com.social_media.identityservice.application.exception.user;

import com.social_media.common.exception.AppException;
import com.social_media.identityservice.api.exception.IdentityErrorCode;

public class UserNotFoundException extends AppException {
    public UserNotFoundException() {
        super(IdentityErrorCode.USER_NOT_FOUND);
    }
}
