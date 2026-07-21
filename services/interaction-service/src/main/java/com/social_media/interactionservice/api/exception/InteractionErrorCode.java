package com.social_media.interactionservice.api.exception;

import org.springframework.http.HttpStatus;

public enum InteractionErrorCode {
    INVALID_REQUEST(46000, HttpStatus.BAD_REQUEST, "Invalid request"),
    VALIDATION_FAILED(46001, HttpStatus.BAD_REQUEST, "Request validation failed"),
    TARGET_NOT_FOUND(46002, HttpStatus.NOT_FOUND, "Target not found"),
    REACTION_CONFLICT(46003, HttpStatus.CONFLICT, "Reaction state conflict"),
    DEPENDENCY_UNAVAILABLE(46004, HttpStatus.SERVICE_UNAVAILABLE, "Dependency unavailable"),
    ROUTE_NOT_FOUND(46005, HttpStatus.NOT_FOUND, "API route not found"),
    METHOD_NOT_ALLOWED(46006, HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed"),
    UNSUPPORTED_MEDIA_TYPE(46007, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type"),
    INTERNAL_ERROR(46999, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");

    private final int code;
    private final HttpStatus status;
    private final String message;

    InteractionErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
