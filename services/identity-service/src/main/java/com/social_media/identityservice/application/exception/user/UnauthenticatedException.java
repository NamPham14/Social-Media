package com.social_media.identityservice.application.exception.user;

import com.social_media.common.exception.AppException;
import com.social_media.identityservice.api.exception.IdentityErrorCode;

public class UnauthenticatedException extends AppException {
    public UnauthenticatedException() {
        super(IdentityErrorCode.UNAUTHENTICATED);
    }
}
