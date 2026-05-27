package com.social_media.postservice.domain.exception;

import org.springframework.http.HttpStatus;

public class ErrorCloudary extends AppException {
    public ErrorCloudary(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
