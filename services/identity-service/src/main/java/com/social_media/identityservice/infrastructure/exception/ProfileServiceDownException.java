package com.social_media.identityservice.infrastructure.exception;

public class ProfileServiceDownException extends RuntimeException {
    public ProfileServiceDownException(String message) {
        super(message);
    }
}
