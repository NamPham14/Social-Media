package com.social_media.postservice.domain.exception;

import org.springframework.http.HttpStatus;

public class EmptyException extends AppException {

    public EmptyException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
