package com.social_media.commentservice.application.command;

import java.util.UUID;

public record CreateCommentCommand(
        UUID postId,
        UUID actorId,
        UUID parentId,
        String content
) {
}
