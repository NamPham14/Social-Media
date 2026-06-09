package com.social_media.postservice.application.command;

import com.social_media.postservice.domain.valueobject.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportPostCommand {

    private UUID postId;
    private UUID reporterId;
    private ReportReason reason;
    private String description;
}
