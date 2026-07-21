package com.social_media.commentservice.api.exception;

import org.springframework.http.HttpStatus;

public enum CommentErrorCode {
    INVALID_REQUEST(45000, HttpStatus.BAD_REQUEST, "Invalid request"),
    VALIDATION_FAILED(45001, HttpStatus.BAD_REQUEST, "Request validation failed"),
    COMMENT_NOT_FOUND(45002, HttpStatus.NOT_FOUND, "Comment not found"),
    TARGET_NOT_FOUND(45003, HttpStatus.NOT_FOUND, "Target not found"),
    COMMENT_ACCESS_DENIED(45004, HttpStatus.FORBIDDEN, "Comment access denied"),
    INTERNAL_ACCESS_DENIED(45005, HttpStatus.FORBIDDEN, "Internal access denied"),
    COMMENT_CONFLICT(45006, HttpStatus.CONFLICT, "Comment state conflict"),
    DEPENDENCY_UNAVAILABLE(45007, HttpStatus.SERVICE_UNAVAILABLE, "Dependency unavailable"),
    ROUTE_NOT_FOUND(45008, HttpStatus.NOT_FOUND, "API route not found"),
    METHOD_NOT_ALLOWED(45009, HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed"),
    UNSUPPORTED_MEDIA_TYPE(45010, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type"),
    INTERNAL_ERROR(45999, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");

    private final int code;
    private final HttpStatus status;
    private final String message;

    CommentErrorCode(int code, HttpStatus status, String message) {
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
