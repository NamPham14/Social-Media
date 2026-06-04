package com.social_media.postservice.application.command;

import com.social_media.postservice.domain.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;



@Getter
@Setter
public class ChangePostVisibilityCommand {
    private UUID postId;
    private UUID userId;
    private Post.Status newStatus;
}
