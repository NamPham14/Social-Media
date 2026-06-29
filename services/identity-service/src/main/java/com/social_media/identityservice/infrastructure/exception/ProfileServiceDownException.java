package com.social_media.identityservice.infrastructure.exception;

import com.social_media.common.exception.ServiceUnavailableException;

public class ProfileServiceDownException extends ServiceUnavailableException {
    public ProfileServiceDownException(String message) {
        super("Profile Service");
    }
}
