package com.social_media.identityservice.application.exception.user;

import com.social_media.common.exception.AppException;
import com.social_media.identityservice.api.exception.IdentityErrorCode;

public class UserExistedException extends AppException {
    public UserExistedException() {
        super(IdentityErrorCode.USER_EXISTED);
    }
}
