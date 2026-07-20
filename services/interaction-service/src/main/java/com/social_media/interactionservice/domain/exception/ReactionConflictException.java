package com.social_media.interactionservice.domain.exception;

public class ReactionConflictException extends RuntimeException {
    public ReactionConflictException(String message) { super(message); }
}
