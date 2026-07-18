package com.social_media.commentservice.domain.exception;

public class InternalAccessDeniedException extends RuntimeException {
    public InternalAccessDeniedException() { super("Valid internal service credential is required"); }
}
