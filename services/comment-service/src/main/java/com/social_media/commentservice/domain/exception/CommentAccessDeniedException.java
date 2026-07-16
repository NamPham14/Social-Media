package com.social_media.commentservice.domain.exception;

public class CommentAccessDeniedException extends RuntimeException {
    public CommentAccessDeniedException() {
        super("Only the comment owner can perform this action");
    }
}
