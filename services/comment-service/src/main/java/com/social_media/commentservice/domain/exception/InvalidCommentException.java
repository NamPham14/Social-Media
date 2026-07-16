package com.social_media.commentservice.domain.exception;

public class InvalidCommentException extends RuntimeException {
    public InvalidCommentException(String message) {
        super(message);
    }
}
