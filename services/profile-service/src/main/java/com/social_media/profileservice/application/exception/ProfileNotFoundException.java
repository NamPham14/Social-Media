package com.social_media.profileservice.application.exception;


import com.social_media.common.exception.AppException;
import com.social_media.profileservice.api.exception.ProfileErrorCode;

public class ProfileNotFoundException extends AppException {
    public ProfileNotFoundException() {
        super(ProfileErrorCode.PROFILE_NOT_FOUND);
    }
}
