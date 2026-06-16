package com.social_media.profileservice.application.exception;

import com.social_media.common.base.BaseErrorCode;
import com.social_media.common.exception.AppException;
import com.social_media.profileservice.api.exception.ProfileErrorCode;

public class DuplicateProfileException extends AppException {
    public DuplicateProfileException() {
        super(ProfileErrorCode.PROFILE_ALREADY_EXISTS);
    }
}
