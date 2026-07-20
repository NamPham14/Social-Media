package com.social_media.commentservice.domain.exception;

import java.util.UUID;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(UUID id) {
        super("Comment '" + id + "' does not exist");
    }
}
