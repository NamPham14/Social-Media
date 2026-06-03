package com.social_media.postservice.application.command;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;



@Getter
@Setter
public class RejectPostCommand {
    private UUID postId;
    private UUID adminId;
    private String reason;
}
