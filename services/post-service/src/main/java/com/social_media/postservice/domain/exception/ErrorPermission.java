package com.social_media.postservice.domain.exception;

import org.springframework.http.HttpStatus;

public class ErrorPermission extends AppException {
    public ErrorPermission(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
