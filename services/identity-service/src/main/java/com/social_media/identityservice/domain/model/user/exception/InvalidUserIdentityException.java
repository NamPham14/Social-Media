package com.social_media.identityservice.domain.model.user.exception;


public class InvalidUserIdentityException extends RuntimeException {
    public InvalidUserIdentityException(String message) {
        super(message);
    }
}
